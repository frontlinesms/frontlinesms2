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
		createActivityDialog { module CreateActivityDialog }
		pollDialog { module PollDialog }
	}
}

class BodyMenu extends geb.Module {
	static content = {
		selected { $('#body-menu .selected').text().toLowerCase() }
		activityLinks { $('#body-menu li.activities ul.submenu li a') }
		newActivity { $('#body-menu a#create-new-activity') }
		newFolder { $('#body-menu li.folders a.btn.create') }
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

abstract class MediumPopup extends geb.Module {
	static base = { $('div.ui-dialog') }
	static content = {
		cancel { $('button#cancel') }
		next { $('button#nextPage') }
		previous { $('button#prev') }
		send { $('button#submit') }
		tab { tabId -> 
			$('#tabs a[href="#tabs-'+tabId+'"]')
		}
		errorPanel { $('div.error-panel') }
		error { errorPanel.text().toLowerCase() }
	}
}

class QuickMessageDialog extends MediumPopup {
	static content = {
		compose { module QuickMessageComposeTab }
		recipients { module QuickMessageRecipientsTab }
		confirm { module QuickMessageConfirmTab }
	}
}

class QuickMessageComposeTab extends geb.Module {
	static base = { $('div#tabs-1') }
	static content = {
		textArea { $('textarea#messageText') }
	}
}

class QuickMessageRecipientsTab extends geb.Module {
	static base = { $('div#tabs-2') }
	static content = {
		
	}
}

class QuickMessageConfirmTab extends geb.Module {
	static base = { $('div#tabs-3') }
	static content = {
		recipientName { $('td#recipient').text() }
	}
}

class CreateActivityDialog extends geb.Module {
	static base = { $('div.ui-dialog') }
	static content = {
		poll { $('input[value="poll"]') }
		announcement { $('input[value="announcement"]') }
		autoreply { $('input[value="autoreply"]') }
	}
}

class PollDialog extends MediumPopup {
	static base = { $('div.ui-dialog') }
	static content = {
		compose { module PollComposeTab }
		response { module PollResponseTab }
		sort { module PollSortTab }
		autoreply { module PollAutoReplyTab }
		edit { module PollEditMessageTab }
		recipients { module PollRecipientsTab }
		confirm { module PollConfirmTab }
		summary { module PollSummary }
	}
}

class PollComposeTab extends geb.Module {
	static base = { $('div#tabs-1') }
	static content = {
		yesNo { $('div.input input[value="yesNo"]') }
		multiple { $('div.input input[value="multiple"]') }
		question { $('textarea#question') }
		dontSendQuestion { $('input#dontSendMessage') }
	}
}

class PollResponseTab extends geb.Module {
	static base = { $('div#tabs-2') }
	static content = {
		choice { choiceLetter -> 
			$('input#choice${choiceLetter}"]')
		}
	}
}

class PollSortTab extends geb.Module {
	static base = { $('div#tabs-3') }
	static content = {
		dontSort { $('ul.select input[value="false"]') }
		sort { $('ul.select input[value="true"]') }
		keyword { $('input#poll-keyword') }
	}
}

class PollAutoReplyTab extends geb.Module {
	static base = { $('div#tabs-4') }
	static content = {
		autoreplyCheck { $('input#enableAutoreply') }
		text { $('textarea#autoreplyText') }
		keyword { $('input#poll-keyword') }
	}
}

class PollEditMessageTab extends geb.Module {
	static base = { $('div#tabs-5') }
	static content = {
		autoreplyCheck { $('textarea#messageText') }
	}
}

class PollRecipientsTab extends geb.Module {
	static base = { $('div#tabs-6') }
	static content = {
		addNumber { $('input#address') }
	}
}

class PollConfirmTab extends geb.Module {
	static base = { $('div#tabs-7') }
	static content = {
		pollName { $('input#name') }
	}
}

class PollSummary extends geb.Module {
	static base = { $('div.sumamry') }
	static content = {

	}
}