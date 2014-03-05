package frontlinesms2.popup

import frontlinesms2.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

abstract class MediumPopup extends geb.Page {
	static content = {
		popupTitle {
			$('#ui-dialog-title-modalBox').text()?.toLowerCase()
		}
		cancel { $('button#cancel') }
		next { $('button#nextPage') }
		previous(required:false) { $('button#prevPage') }
		submit { $('button#submit') }
		tab { tabId -> 
			$('#tabs a[href="#tabs-'+tabId+'"]')
		}
		tabByName { tabName ->
			$("#tabs a.tab-${tabName}")
		}
		errorPanel { $('div.error-panel') }
		validationError { $('label.error') }
		error { errorPanel.text()?.toLowerCase() }
		thinking(required: false) { $('#thinking') }
	}
}

class QuickMessageDialog extends MediumPopup {
	static at = {
		popupTitle.contains("message") || popupTitle.contains("forward") || popupTitle.contains("reply")
	}
	static content = {
		errorPanel { $(".error-panel") }
		textArea { $('textArea[name=messageText]') }
		recipients { module QuickMessageRecipientSelector } 
		charCount { $('div.character-count-display').text() }
		magicWand { $("#magicwand-selectsendMessageText") }
		messagesToSendCount { $('#messages-count').text() }
		recipientName { $('td#recipient').text() }
	}
}

class QuickMessageRecipientSelector extends RecipientsTab {
	static base = { $('div#tabs-1') }
}

class GenericComposeTab extends geb.Module {
	static base = { $('div#tabs-1') }
	static content = {
		textArea { $('textarea#messageText') }
		wordCount { $("span#send-contact-infos").text() }
		magicWand { $("#magicwand-selectmessageText") }
	}
}

class CreateActivityDialog extends MediumPopup {
	static at = {
		popupTitle.contains("popup.activity.create")
	}
	static content = {
		poll { $('input[value="poll"]') }
		announcement { $('input[value="announcement"]') }
		autoreply { $('input[value="autoreply"]') }
		autoforward { $('input[value="autoforward"]') }
		webconnection(wait:true) { $('input[value="webconnection"]') }
		subscription { $('input[value="subscription"]') }
		customactivity { $('input[value="customactivity"]') }
	}
}

class PollDialog extends MediumPopup {
	static at = {
		popupTitle.contains("poll") || popupTitle.contains("wizard.fmessage.edit.title")
	}
	static content = {
		compose { module ComposeTab }
		response { module ResponseTab }
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
		popupTitle.contains("wizard.fmessage.edit.title")
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

class SortTab extends geb.Module {
	static base = { $('div#tabs-3') }
	static content = {
		dontSort { $('ul.select input[value="false"]') }
		sort { $('ul.select input[value="true"]') }
		toggle { $('input#enableKeyword') }
		keyword { $('input#poll-keyword') }
		labels { $('#poll-aliases label') }
		inputs { $('#poll-aliases input.keywords') }
		pollKeywordsContainer { $('#poll-keywords') }
	}
}

class AutoReplyTab extends geb.Module {
	static base = { $('div#tabs-4') }
	static content = {
		autoreplyCheck { $('input#enableAutoreply') }
		text { $('textarea#autoreplyText') }
		keyword { $('input#poll-keyword') }
	}
}

class EditMessageTab extends geb.Module {
	static base = { $('div#tabs-5') }
	static content = {
		text { $('textarea#messageText') }
	}
}

class RecipientsTab extends geb.Module {
	static base = { $('div#tabs-6') }
	static content = {
		chosenInput { $('.chzn-container input[type=text]') }
		chosenOption { label=null ->
			if (label) {
				label = label.replace("+", "\\+")
				def pattern = java.util.regex.Pattern.compile("(${label}|${label}\\s\\(\\d+\\)|\"${label}\")\$")
				$('.chzn-container ul.chzn-results li.active-result', text: pattern)
			}
			else
				$('.chzn-container ul.chzn-results li.active-result')
		}
		recipientsSelect { $('[name=recipients]') }
		noResultsIndicator { $('ul.chzn-results li.no-results') }
		getRecipients { type = null ->
			def recipients = [contact:[], address:[], group:[], smartgroup:[]]
			recipientsSelect.value().each {
				def k = it.split('-')[0]
				def v = it.split('-')[1]
				recipients[k] << v
			}
			return (type? recipients[type] : recipients)
		}
		addRecipient { searchString ->
			chosenInput.click()
			chosenInput.value(searchString)
			chosenInput.jquery.trigger('keyup')
			try {
				waitFor { chosenOption(searchString).displayed || noResultsIndicator.displayed }
			}
			catch(geb.waiting.WaitTimeoutException e) {
				// TODO drop this try catch once the empty optgroup bug is fixed, as the waitFor then covers all expected scenarios
				// https://frontlinesms.jira.com/browse/TOOLS-737
				return false
			}
			if( chosenOption(searchString).displayed) {
				chosenOption(searchString).jquery.trigger("mouseup")
				return true
			}
			else {
				return false
			}
		}
		removeRecipient { label ->
			if (!(['group', 'contact'].any { label.contains(it) }))
				label = "\"$label\""
			$('ul.chzn-choices li.search-choice', text: label).find('a.search-choice-close').click()
		}
		manual { $('li.manual.contact') }
		count { getRecipients().values().sum { it.size() } }
	}
}

class ConfirmTab extends geb.Module {
	static base = { $('div#tabs-7') }
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
		$('#ui-dialog-title-modalBox').text()?.toLowerCase().contains("export");
	}
	static content = {
	}
}

class RenameDialog extends MediumPopup {
	static at = {
		$('#ui-dialog-title-modalBox').text()?.toLowerCase().contains("rename");
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
		composeAnnouncement {module GenericComposeTab}
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
	static base = { $('div#tabs-4') }
	static content = {
		message { $("div.summary") }
	}
}

class DeleteDialog extends MediumPopup {
	static at = {
		popupTitle.contains("smallpopup.empty.trash.prompt");
	}
	static content = {
		title { $("#title").text() }
		done { $('button#done') }
	}
}

class SmartGroupCreateDialog extends MediumPopup {
	static at = {
		popupTitle == 'popup.smartgroup.create'
	}
	static content = {
		rules { $('tr.smart-group-criteria') }
		ruleField { rules.find('select', name:'rule-field') }
		ruleValues { rules.find('input', name:'rule-text') }
		ruleMatchText { rules.find('.rule-match-text')*.text() }
		removeRuleButtons(required:false) { $('tr.smart-group-criteria a.remove-command') }
		smartGroupNameField { $('input', type:'text', name:'smartgroupname') }
		addRuleButton { $('.btn', text:'smartgroup.add.anotherrule') }
		updateButton { $('button', text:'action.update') }
		editButton { $('button', text:'action.edit') }
		flashMessage(required:false) { $('div.flash') }
	}
}


class SmartGroupEditDialog extends SmartGroupCreateDialog {
	static at = {
		popupTitle == 'smallpopup.group.edit.title'
	}
}

class WebconnectionWizard extends MediumPopup {
	static at = {
		waitFor('very slow') { popupTitle.contains("connection") || popupTitle == 'wizard.fmessage.edit.title' }
		return true
	}
	static content = {
		error { $("label.error").text()}
		keywordTab { module WebconnectionKeywordTab }
		requestTab { module WebconnectionRequestFormatTab }
		apiTab { module WebconnectionAPITab }
		confirmTab(required:false) { module WebconnectionConfirmTab }
		summary { module WebconnectionSummary }

		configureUshahidi(required:false) { module ConfigureUshahidiWebconnectionTab }

		option(wait:true, cache:false) { shortName -> $('input', name:'webconnectionType', value:shortName) }
		getTitle { shortName -> option(shortName).previous('h3').text() }
		getDescription { shortName -> option(shortName).previous('p.info').text() }
		testConnectionButton(required:false) { $("#testRoute")}
	}
}

class WebconnectionKeywordTab extends geb.Module {
	static base = { $('div.generic_sorting_tab') }
	static content = {
		useKeyword { value ->
				$('input#sorting',value:value)
		}
		keyword { $('input#keywords') }
	}
}

class WebconnectionRequestFormatTab extends geb.Module {
	static base = { $('div#tabs-2') }
	static content = {
		post { $("input[value='POST']") }
		get { $("input[value='GET']") }
		url { $("input#url") }
		addParam { $('a.btn.addNew') }
		parameters { moduleList WebconnectionParam, $('.web-connection-param-table tbody tr') }
	}
}

class WebconnectionAPITab extends geb.Module {
	static base = { $('div#webconnection-api') }
	static content = {
		enableApi { $("#enableApi") }
		secret { $("#secret") }
	}
}

class WebconnectionParam extends geb.Module {
	static content = {
		value { $('input.param-value') }
		name { $("input.param-name") }
		remove { $("a.remove-command") }
	}
}

class WebconnectionConfirmTab extends geb.Module {
	static base = { $('div#webconnection-confirm') }
	static content = {
		name { $('input#name') }
		keyword { $("#confirm-keyword").text() }
		type { $("#confirm-type").text() }
		url { $("#confirm-url").text() }
		frontline_api_key { $("#confirm-fsmskey").text() }
		crowdmap_api_key { $("#confirm-crowdmapkey").text() }
		
		confirm{ label->
			$("#confirm-"+label).text()
		}
	}
}

class WebconnectionSummary extends geb.Module {
	static content = {
		message { $("p#webconnection-dialog-summary") }
	}
}

class SubscriptionCreateDialog extends MediumPopup {
	static at = {
		popupTitle.contains("subscription") || popupTitle.contains("wizard.fmessage.edit.title")
	}
	static content = {
		group { module SubscriptionGroupTab }
		keywords { module SubscriptionKeywordsTab}
		autoreply { module SubscriptionAutoReplyTab }
		confirm { module SubscriptionConfirmTab }
		summary { module SubscriptionSummary }
		error { errorPanel }
	}
}

class SubscriptionGroupTab extends geb.Module {
	static base = { $('div#tabs-1') }
	static content = {
		addToGroup { groupId ->
			$('select#subscriptionGroup').jquery.val(groupId)
			$('select#subscriptionGroup').jquery.trigger("change")
		}
		createGroup {
			addToGroup 'create_group'
		}
		newGroupName { $('input#groupName') }
		newGroupSubmit { $('a.create-group') }
		groupNameError(required:false) { $('label.error[for=groupName]') }
	}
}

class SubscriptionKeywordsTab extends geb.Module {
	static base = { $('div#tabs-2')}
	static content = {
		keywordText { $('input#topLevelKeywords') }
		joinKeywords {$('input#joinKeywords')}
		leaveKeywords {$('input#leaveKeywords')}
		defaultAction { $("input#defaultAction") }
	}
}

class SubscriptionAutoReplyTab extends geb.Module {
	static base = { $('div#tabs-3') }
	static content = {
		enableJoinAutoreply {$('input#enableJoinAutoreply')}
		joinAutoreplyText {$('textarea#joinAutoreplyText')}
		enableLeaveAutoreply{$('input#enableLeaveAutoreply')}
		leaveAutoreplyText {$('textarea#leaveAutoreplyText')}
	}
}

class SubscriptionConfirmTab extends geb.Module {
	static base = { $('div#tabs-4') }
	static content = {
		subscriptionName { $('input#name') }
		keyword {$("#confirm-keyword").text()}
		confirm {id->
			$("#confirm-"+id).text()
		}
		joinAliases {$("#confirm-joinAliases").text()}
		leaveAliases {$("#confirm-leaveAliases").text()}
		autoreplyText {$("#confirm-autoreplyText").text()}
	}
}

class SubscriptionSummary extends geb.Module {
	static base = { $('div#tabs-5') }
	static content = {
		message { $("div.summary") }
		ok { $('button#submit') }
	}
}

class EditSubsriptionDialog extends SubscriptionCreateDialog {
	static at = {
		popupTitle.contains('edit subscription')
	}
}

class WebconnectionTypeSelectTab extends geb.Module{
	static base = { $('div#tabs-1') }
	static content = {
		getDescription { shortName ->
			$("#"+shortName).text()
		}
		getTitle { shortName ->
			$("#"+shortName+" .info").text()
		}
		option { shortName ->
			$('#webconnectionType').value(shortName)
		}
	}
}

class ConfigureUshahidiWebconnectionTab extends geb.Module{
	static base = { $('#webconnection-config') }
	static content = {
		subType(required:false){ type->
			$('input', name:'serviceType', value:type)
		} 
		crowdmapDeployAddress { $('#displayed_url') }
		ushahidiDeployAddress{ $('#displayed_url') }
		ushahidiKeyLabel { $("label", for:'key').first() }
		crowdmapKeyLabel { $("label", for:'key').last() }
		urlSuffix { $("label", for:'url').last() }
		crowdmapApiKey{ $('#key') }
		ushahidiApiKey{ $('#key') }
	}
}

class AutoreplyCreateDialog extends MediumPopup {
	static at = {
		popupTitle.contains("autoreply") || popupTitle.contains("wizard.fmessage.edit.title")
	}
	static content = {
		message { module AutoreplyMessageTab}
		keyword { module AutoreplyKeywordTab}
		confirm { module AutoreplyConfirmTab}
		summary { module AutoreplySummaryTab}
		validationErrorText { $('label.error').text() }
		errorText { errorPanel.text()?.toLowerCase() }
		error { errorPanel }
		create { $('button#submit') }
	}
}

class AutoreplyMessageTab extends geb.Module {
	static base = { $('div#tabs-1')}
	static content = {
		messageText { $('area#messageText') }
	}
}

class AutoreplyKeywordTab extends geb.Module {
	static base = { $('div#tabs-2')}
	static content = {
		keywordText { $('#keywords') }
		blankKeyword {$('#blankKeyword')}
	}
}

class AutoreplyConfirmTab extends geb.Module {
	static base = { $('div#tabs-3') }
	static content = {
		keywordConfirm {$("#keyword-confirm").text()}
		autoreplyConfirm {$("#autoreply-confirm").text()}
		nameText {$("#name")}
	}
}

class AutoreplySummaryTab extends geb.Module {
	static base = { $('div#tabs-4 > div.summary') } //ensures div.summary has been loaded too
	static content = {
		message { $("p", 0).text() }
	}
}

class NewFeaturesDialog extends MediumPopup {
	static at = {
		popupTitle.contains("feature")
	}
	static content = {
	}
}

class ImportContactDialog extends MediumPopup {
	static at = {
		popupTitle.contains("contact.import.label")
	}
	static content = {
	}
}

class AutoforwardCreateDialog extends MediumPopup {
	static at = {
		popupTitle.contains("autoforward") || popupTitle.contains("wizard.fmessage.edit.title")
	}
	static content = {
		message { module AutoforwardMessageTab}
		keyword { module AutoforwardKeywordTab}
		recipients { module AutoforwardRecipientsTab}
		confirm { module AutoforwardConfirmTab}
		summary { module AutoforwardSummaryTab}
		validationErrorText { $('label.error').text() }
		errorText { errorPanel.text()?.toLowerCase() }
		error { errorPanel }
		create { $('button#submit') }
	}
}

class AutoforwardMessageTab extends geb.Module {
	static base = { $('div#tabs-1') }
	static content = {
		messageText { $('#messageText') }
	}
}

class AutoforwardKeywordTab extends geb.Module {
	static base = { $('div#tabs-2')}
	static content = {
		keywordText { $('#keywords') }
		blankKeyword {$('#blankKeyword')}
	}
}

class AutoforwardRecipientsTab extends RecipientsTab {
	static base = { $('div#tabs-3')}
}

class AutoforwardConfirmTab extends geb.Module {
	static base = { $('div#tabs-4') }
	static content = {
		nameText { $("#name") }
		keywordConfirm { $("#keyword-confirm").text() }
		recipientCount { $("#autoforward-confirm-recipient-count").text() }
	}
}

class AutoforwardSummaryTab extends geb.Module {
	static base = { $('div#tabs-5') }
	static content = {
		message { $("div.summary") }
	}
}

