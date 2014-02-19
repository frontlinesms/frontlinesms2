package frontlinesms2.message

import frontlinesms2.*
import static frontlinesms.grails.test.EchoMessageSource.parseDate

abstract class PageMessage extends frontlinesms2.page.PageBase {
	static url = 'message/'
	static content = {
		bodyMenu { module BodyMenu }
		header { module ContentHeader }
		footer { module ContentFooter }
		messageListHeader { module MessageListHeader }
		messageList { module MessageList }
		singleMessageDetails { module SingleMessageDetails }
		multipleMessageDetails { module MultipleMessageDetails }
		flashMessage(required:false) { $('div.flash') }
	}
}

class BodyMenu extends geb.Module {
	static content = {
		messageSection { section ->
			$("a", text: "${section}")
		}
		inboxNewMessageCount { $('#body-menu span.unread_message_count')[0].text() as Integer }
		pendingMessageCount { $('#body-menu span.pending_message_count')[0].text() as Integer }
		selected { $('#body-menu .selected').text()?.toLowerCase() }
		activityList { $('#body-menu li.activities ul.submenu li') }
		activityLinks { $('#body-menu li.activities ul.submenu li a') }
		newActivity { $('#body-menu a#create-new-activity') }
		newFolder { $('#body-menu li.folders a.btn.create') }
		folderLinks { $('ul li.folders ul.submenu li a') }
		activityLink { activityName ->
			$('#body-menu li.activities ul.submenu li a', text: startsWith("${Activity.findByName(activityName)?.shortName}.title[${activityName}]"))
		}
		sectionLink { sectionName ->
			$('#body-menu li.messages ul.submenu li a', text: startsWith("fmessage.section.${sectionName.toLowerCase()}"))
		}
	}
}

class ContentHeader extends geb.Module {
	static base = { $('#main-list-head') }
	static content = {
		title { $('h1').text()?.toLowerCase() }
		buttons { $('a.btn, input[type="button"], button') }
		export (required:false) { $('#export-btn a') }
		moreActions { $('div.header-buttons select#more-actions') }
		searchDescription { $('p', class:'description').text() }
		quickMessage { $("a#quick_message") }
	}
}

class ContentFooter extends geb.Module {
	static base = { $('#main-list-foot') }
	static content = {
		showAll { $('a')[0] }
		showStarred { $('a')[1] }
		showOutgoing { $('a')[3] }
		showIncoming { $('a')[2] }
		nextPage { $('a.nextLink') }
		prevPage { $('a.prevLink') }
	}
}

class MessageListHeader extends geb.Module {
	static base = { $('#main-list thead') }
	static content = {
		source { $('#source-header a') }
		message { $('#message-header a') }
	}
}

class MessageList extends geb.Module {
	static base = { $('#main-list') }
	static content = {
		selectAll { $("input#interaction-select-all") }
		messageSource { i=0 -> message(i).find('td.message-sender-cell').text() }
		message { i=0, onlySelected=false -> onlySelected? $('tbody tr.selected', i): $("tbody tr:nth-of-type(${i+1})") }
		messageCount { js.exec('return jQuery("#main-list tbody tr").size()') as Integer }
		selectedMessageCount { js.exec('return jQuery("#main-list tbody tr.selected").size()') as Integer }
		clickLink { i=0 -> message(i).find('td.message-text-cell a').click(); true }
		selectedMessageLinkUrl { i=0 -> message(i, true).find('td.message-text-cell a').@href }
		getCheckbox { i -> message(i).find('input[type=checkbox]') }
		isChecked { i -> getCheckbox(i).checked }
		isRead { i -> hasClass(i, 'read') }
		toggleSelect { i -> getCheckbox(i).click(); true }
		hasClass { i, cssClass -> message(i).hasClass(cssClass) }
		getDateCell { i -> message(i).find('td.message-date-cell') }
		messageDate { i=0 ->
			parseDate(getDateCell(i).text())
		}
		messageText { i=0, onlySelected=false -> message(i, onlySelected).find('td.message-text-cell').text() }
		selectedMessageText { i=0 -> messageText(i, true) }
		noContent { $('tr.no-content') }
		starFor { message ->
			if (message instanceof TextMessage) {
				return $("tr #star-${message.id} a")
			} else if(message instanceof Number) {
				return $("tr #star-${message} a")
			}
		}
		displayedNameFor { m ->
			def id = m instanceof TextMessage? m.id: m
			return $(".displayName-${id}").text()
		}
		newMessageNotification(required:false) { $("#new-message-notification") }
	}
}

class SingleMessageDetails extends geb.Module {
	static base = { $('#single-interaction') }
	static content = {
		noneSelected { $('#interaction-detail-content').text() == 'fmessage.selected.none' }
		sender { $('#interaction-detail-sender').text() }
		senderLink { $('#interaction-detail-sender a') }
		addToContacts(required:false) { $('#add-contact') }
		text { $('#interaction-detail-content').text() }
		date {
			parseDate($('#interaction-detail-date').text())
		}
		archive(required:false) { $('#archive-msg') }
		unarchive { $('#unarchive-msg') }
		reply { $('a#btn_reply') }
		retry { $('a#btn_retry') }
		forward { $('#btn_forward') }
		delete(required:false) {$('#delete-msg')}
		single_moveActions(required:false) { $("select#move-actions") }
		moveTo { msgowner -> 
			$('select#move-actions').jquery.val(msgowner)
			$('select#move-actions').jquery.trigger("change")
		}
		moveActions { $('select#move-actions option')*.text() }
		receivedOn(required:false) { $("#interaction-detail-fconnection") }
	}
}

class MultipleMessageDetails extends geb.Module {
	static base = { $('#multiple-interactions') }
	static content = {
		text { $('#interaction-detail-content').text() }
		checkedMessageCount { ($('p#checked-message-count').text() =~ /message\.multiple\.selected\[(\d+)]/)[0][1].toInteger() }
		replyAll(required:false) { $('a#btn_reply_all') }
		retry { $("input#retry-failed") }
		deleteAll {$('#btn_delete_all')}
		archiveAll(required:false) { $('#btn_archive_all') }
		multiple_moveActions(required:false) { $("select#move-actions") }
		moveTo { msgowner -> 
			$('select#move-actions').jquery.val(msgowner)
			$('select#move-actions').jquery.trigger("change")
		}
		moveActions { $('select#move-actions option')*.text() }
	}
}

