package frontlinesms2.message

import frontlinesms2.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

abstract class PageMessage extends frontlinesms2.base.PageBase {
	static url = 'message/'
	static content = {
		bodyMenu { module BodyMenu }
		contentHeader { module ContentHeader }
		messageList { module MessageList }
		singleMessageDetails { module SingleMessageDetails }
		multipleMessageDetails { module MultipleMessageDetails }
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

class MessageList extends geb.Module {
	static base = { $('#main-list') }
	static content = {
		sources { $('td.message-sender-cell')*.text() }
		messages { moduleList MessageListRow, $('tr') }
		selectedMessages { moduleList MessageListRow, $('tr.selected') }
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
	}
}

class MultipleMessageDetails extends geb.Module {
	static base = { $('#multiple-messages') }
	static content = {

	}
}