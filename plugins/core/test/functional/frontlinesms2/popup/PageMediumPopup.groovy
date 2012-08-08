package frontlinesms2.popup

import frontlinesms2.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

abstract class MediumPopup extends geb.Page {
	static content = {
		popupTitle {
			$('#ui-dialog-title-modalBox').text().toLowerCase()
		}
		cancel { $('button#cancel') }
		next { $('button#nextPage') }
		previous { $('button#prevPage') }
		submit { $('button#submit') }
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
		errorPanel { $(".error-panel") }
	}
}

class QuickMessageComposeTab extends geb.Module {
	static base = { $('div#tabs-1') }
	static content = {
		textArea { $('textarea#messageText') }
		wordCount { $("span#send-message-stats").text() }
		magicWand { $("#magicwand-selectmessageText") }
	}
}

class QuickMessageRecipientsTab extends geb.Module {
	static base = { $('div#tabs-2') }
	static content = {
		addField { $('input#address') }
		addButton { $('a.btn.add-address') }
		manual { $('li.manual.contact') }
		count { $('#recipient-count').text().toInteger() }
		manualContacts { $("li.manual").find("input", name:"addresses") }
		groupCheckboxes { $('input', type:'checkbox', name:'groups') }
		recipientCheckboxByValue { val -> $("input[value='" + val + "']") }
	}
}

class QuickMessageConfirmTab extends geb.Module {
	static base = { $('div#tabs-3') }
	static content = {
		messagesToSendCount { $('#contacts-count').text() }
		recipientName { $('td#recipient').text() }
	}
}

class CreateActivityDialog extends MediumPopup {
	static at = {
		popupTitle.contains("create new activity")
	}
	static content = {
		poll { $('input[value="poll"]') }
		announcement { $('input[value="announcement"]') }
		autoreply { $('input[value="autoreply"]') }
	}
}

class PollDialog extends MediumPopup {
	static at = {
		popupTitle.contains("activity")
	}
	static content = {
		compose { module ComposeTab }
		response { module ResponseTab }
		aliases { module AliasTab }
		sort { module SortTab }
		autoreply { module AutoReplyTab }
		edit { module EditMessageTab }
		recipients { module RecipientsTab }
		confirm { module ConfirmTab }
		summary { module Summary }
	}
}

class EditPollDialog extends PollDialog {
	static at = {
		popupTitle.contains("edit activity")
	}
}

class ComposeTab extends geb.Module {
	static base = { $('div#tabs-1') }
	static content = {
		yesNo { $('div.input input[value="yesNo"]') }
		multiple { $('div.input input[value="multiple"]') }
		question { $('textarea#question') }
		dontSendQuestion { $('input#dontSendMessage') }
	}
}

class ResponseTab extends geb.Module {
	static base = { $('div#tabs-2') }
	static content = {
		choice { choiceLetter -> 
			$('#choice'+choiceLetter)
		}
		label { choiceLetter ->
			$('label[for="choice'+choiceLetter+'"]')
		}
		errorLabel { choiceLetter ->
			$('label.error[for="choice'+choiceLetter+'"]')
		}
	}
}

class AliasTab extends geb.Module {
	static base = { $('div#tabs-4') }
	static content = {
		labels { $('#poll-aliases label') }
		inputs { $('#poll-aliases input.aliases') }
	}
}

class SortTab extends geb.Module {
	static base = { $('div#tabs-3') }
	static content = {
		dontSort { $('ul.select input[value="false"]') }
		sort { $('ul.select input[value="true"]') }
		toggle { $('input#enableKeyword') }
		keyword { $('input#poll-keyword') }
	}
}

class AutoReplyTab extends geb.Module {
	static base = { $('div#tabs-5') }
	static content = {
		autoreplyCheck { $('input#enableAutoreply') }
		text { $('textarea#autoreplyText') }
		keyword { $('input#poll-keyword') }
	}
}

class EditMessageTab extends geb.Module {
	static base = { $('div#tabs-6') }
	static content = {
		text { $('textarea#messageText') }
	}
}

class RecipientsTab extends geb.Module {
	static base = { $('div#tabs-7') }
	static content = {
		addField { $('input#address') }
		addButton { $('a.btn.add-address') }
		manual { $('li.manual.contact') }
		count { $('#recipient-count').text().toInteger() }
	}
}

class ConfirmTab extends geb.Module {
	static base = { $('div#tabs-8') }
	static content = {
		pollName { $('input#name') }
		message { $("#poll-message").text() }
		recipientCount { $("#confirm-recipients-count").text() }
		noRecipients { $("#no-recipients") }
		messageCount { $("#confirm-messages-count").text() }
		autoreply { $("#auto-reply-read-only-text").text() }
	}
}

class Summary extends geb.Module {
	static base = { $('div.summary') }
	static content = {

	}
}

class ExportDialog extends MediumPopup {
	static at = {
		$('#ui-dialog-title-modalBox').text().toLowerCase().contains("export");
	}
	static content = {

	}
}

class RenameDialog extends MediumPopup {
	static at = {
		$('#ui-dialog-title-modalBox').text().toLowerCase().contains("rename");
	}
	static content = {
		name { $('input#name') }
		done { $('button#done') }
	}
}


class AnnouncementDialog extends MediumPopup {
	static at = {
		popupTitle.contains("announcement")
	}
	static content = {
		composeAnnouncement {module QuickMessageComposeTab}
		recipients {module AnnouncementRecipientsTab}
		confirm { module AnnouncementConfirmTab }
		summary { module AnnouncementSummary }
	}
}

class AnnouncementRecipientsTab extends RecipientsTab {
	static base = { $('div#tabs-2') }
}

class AnnouncementConfirmTab extends geb.Module {
	static base = { $('div#tabs-3') }
	static content = {
		announcementName { $('input#name') }
		message { $("#confirm-message-text").text() }
		recipientCount { $("#confirm-recipients-count").text() }
		recipientCount { $("#confirm-message-count").text() }
	}
}

class AnnouncementSummary extends geb.Module {
	static at = {
		popupTitle.contains("announcement saved")
	}
}

class DeleteDialog extends MediumPopup {
	static at = {
		popupTitle.contains("empty trash");
	}
	static content = {
		title { $("#title").text() }
		done { $('button#done') }
	}
}

class ConnectionDialog extends MediumPopup {
	static at = {
		popupTitle.contains('connection')
	}

	static content = {
		connectionType { $("#connectionType") }
		connectionForm { $('#connectionForm')}
		confirmName { $("#confirm-name")}
		confirmType { $("#confirm-type")}
		confirmPort { $("#confirm-port")}
		confirmIntelliSmsConnectionName { $("#intellisms-confirm #confirm-name")}
		confirmIntelliSmsUserName { $("#intellisms-confirm #confirm-username")}
		confirmIntelliSmsType { $("#intellisms-confirm #confirm-type")}
	}
}

class SmartGroupCreateDialog extends MediumPopup {
	static at = {
		popupTitle.contains('create smart group')
	}

	static content = {

		rules { $('tr.smart-group-criteria') }
		ruleField { rules.find('select', name:'rule-field') }
		ruleValues { rules.find('input', name:'rule-text') }
		ruleMatchText { rules.find('.rule-match-text')*.text() }
		removeRuleButtons(required:false) { $('tr.smart-group-criteria a.remove-command') }
		smartGroupNameField { $('input', type:'text', name:'smartgroupname') }
		addRuleButton { $('.btn', text:"Add another rule") }
		editButton { $('button', text:'Edit')}
		flashMessage(required:false) { $('div.flash') }
	}
}


class SmartGroupEditDialog extends SmartGroupCreateDialog {
	static at = {
		popupTitle.contains('Edit Group')
	}
}

