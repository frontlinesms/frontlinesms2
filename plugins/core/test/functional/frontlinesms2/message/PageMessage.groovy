package frontlinesms2.message

import frontlinesms2.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

abstract class PageMessage extends frontlinesms2.base.PageBase {
	static url = 'message/'
	static content = {
		bodyMenu { module BodyMenu }
		header { module ContentHeader }
		footer { module ContentFooter }
		messageList { module MessageList }
		singleMessageDetails { module SingleMessageDetails }
		multipleMessageDetails { module MultipleMessageDetails }
		quickMessageDialog { module QuickMessageDialog }
	}
}

class BodyMenu extends geb.Module {
	static content = {
		selected { $('#body-menu .selected').text().toLowerCase() }
		activityLinks { $('#body-menu li.activities ul.submenu li a') }
	}
}

class ContentHeader extends geb.Module {
	static base = { $('#main-list-head') }
	static content = {
		title { $('h1').text().toLowerCase() }
		buttons { $('a.btn, input[type="button"], button') }
	}
}

class ContentFooter extends geb.Module {
	static base = { $('#main-list-foot') }
	static content = {
		showAll { $('a')[0] }
		showStarred { $('a')[1] }
		nextPage { $('a.nextLink') }
		prevPage { $('a.prevLink') }
	}
}

class MessageList extends geb.Module {
	static base = { $('#main-list') }
	static content = {
		sources { $('td.message-sender-cell')*.text() }
		messages { moduleList MessageListRow, $('tr') }
		selectedMessages { moduleList MessageListRow, $('tr.selected') }
		noContent { $('td.no-content') }
	}
}

class MessageListRow extends geb.Module {
	static content = {
		checkbox { $('td.message-select-cell input[type="checkbox"]') }
		star { $('a.starred, a.unstarred') }
		isStarred { $('a.starred').count() == 1 }
		isRead { $(':first-child').parent().hasClass('read') } //TODO: replace with a more sensible selector for base
		source { $('td.message-sender-cell').text() }
		text { $('td.message-text-cell').text() }
		date {
			new SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.US).parse($('td.message-date-cell').text())
		}
		linkUrl { $('td.message-text-cell a').@href }
	}
}

class SingleMessageDetails extends geb.Module {
	static base = { $('#single-message') }
	static content = {
		noneSelected { $('#message-detail-content').text().toLowerCase() == "no message selected" }
		sender { $('#message-detail-sender').text() }
		text { $('#message-detail-content').text() }
		date { 
			new SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.US)
				.parse($('#message-detail-date').text())
		}
		reply { $('a#btn_reply') }
		forward { $('#btn_forward') }
		moveTo { foldername -> 
			$('select#move-actions').jquery.val(Folder.findByName(foldername).id.toString())
			$('select#move-actions').jquery.trigger("change")
		}
	}
}

class MultipleMessageDetails extends geb.Module {
	static base = { $('#multiple-messages') }
	static content = {
		checkedMessageCount { $('p#checked-message-count').text().toInteger() }
	}
}

class QuickMessageDialog extends geb.Module {
	static base = { $('div.ui-dialog') }
	static content = {
		compose { module ComposeTab }
		recipients { module RecipientsTab }
		confirm { module ConfirmTab }
		cancel { $('button#cancel') }
		next { $('button#nextPage') }
		previous { $('button#prev') }
		send { $('button#submit') }
	}
}

class ComposeTab extends geb.Module {
	static base = { $('div#tabs-1') }
	static content = {
		textArea { $('textarea#messageText')}
	}
}

class RecipientsTab extends geb.Module {
	static base = { $('div#tabs-2') }
	static content = {
		
	}
}

class ConfirmTab extends geb.Module {
	static base = { $('div#tabs-3') }
	static content = {
		recipientName { $('td#recipient').text() }
	}
}