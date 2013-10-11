package frontlinesms2.customactivity

import geb.Module

import frontlinesms2.popup.MediumPopup

class CustomActivityCreateDialog extends MediumPopup {
	static at = {
		popupTitle in ['default.new.label[customactivity.label]', 'wizard.fmessage.edit.title[customactivity]', 'default.edit.label[customactivity.label]']
	}
	static content = {
		keyword { module ConfigureCustomKeywordTab }
		configure { module ConfigureCustomActivityTab }
		confirm { module CustomActivityConfirmTab }
		summary { module CustomActivitySummaryTab }
		validationErrorText { $('label.error').text() }
		errorText { errorPanel.text()?.toLowerCase() }
		error { errorPanel }
		create { $('button#submit') }
	}
}

class ConfigureCustomActivityTab extends Module {
	static base = { $('div#tabs-2')}
	static content = {
		stepActions { $("#custom_activity_select") }
		steps(required:false) { $("li.step") }
	}
}

class ConfigureCustomKeywordTab extends Module {
	static base = { $('div#tabs-1')}
	static content = {
		keywordText { $('#keywords') }
		blankKeyword {$('#blankKeyword')}
	}
}

class CustomActivityConfirmTab extends Module {
	static base = { $("div#tabs-3") }
	static content = {
		name { $('input#name') }
		keywordConfirm {$("#keyword-confirm").text()}
		stepActionsConfirm { $("#customactivity-confirm-action-steps").text() }
	}
}

class CustomActivitySummaryTab extends Module {
	static base = { $("div#tabs-4") }
	static content = {
		message { $("div.summary") }
	}
}

