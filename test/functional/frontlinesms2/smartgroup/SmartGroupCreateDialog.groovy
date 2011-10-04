package frontlinesms2.smartgroup

class SmartGroupCreateDialog extends geb.Page {
	static at = {
		$("#ui-dialog-title-modalBox").text() == 'Create smart group'
	}
	
	static content = {
		nameField { $('input', name:'name') }		
		rules { $('tr.smartgroup-criteria') }
		ruleField { rules.find('select', name:'field') }
		ruleValues { rules.find('input', type:'textfield') }
		removeRuleButtons { rules.find('.button.remove-rule') }
		
		smartGroupNameField { $('input', type:'text', name:'name') }
		
		addRuleButton { $('.button', text:"Add another rule") }
		
		backButton { $('button', text:'Back') }
		cancelButton { $('button', text:'Cancel') }
		finishButton { $('button', text:'Create') }
		
		errorMessages(required:false) { $('.flash.errors') }
	}
}