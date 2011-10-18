package frontlinesms2.smartgroup

class SmartGroupCreateDialog extends frontlinesms2.contact.PageContactShow {
	static at = {
		$("#ui-dialog-title-modalBox").text() == 'Create smart group'
	}
	
	static content = {	
		rules { $('tr.smart-group-criteria') }
		ruleField { rules.find('select', name:'rule-field') }
		ruleValues { rules.find('input', name:'rule-text') }
		ruleMatchText { rules.find('.rule-match-text')*.text() }
		removeRuleButtons { rules.find('.button.remove-rule') }
		
		smartGroupNameField { $('input', type:'text', name:'smartgroupname') }
		
		addRuleButton { $('.button', text:"Add another rule") }
		
		backButton { $('button', text:'Back') }
		cancelButton { $('button', text:'Cancel') }
		finishButton { $('button', text:'Create') }
		
		errorMessages(required:false) { $('.error-panel') }
		flashMessage(required:false) { $('div.flash') }
	}
}