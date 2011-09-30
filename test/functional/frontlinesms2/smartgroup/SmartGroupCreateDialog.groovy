package frontlinesms2.smartgroup

class SmartGroupCreateDialog extends geb.Page {
	static at = {
		$("#ui-dialog-title-modalBox").text() == 'Create group'
	}
	
	static content = {
		rules { $('ul#smart-group-criteria li') }
		ruleField { rules.find('select', name:'field') }
		ruleValues { rules.find('input', type:'textfield') }
		removeRuleButtons { rules.find('.button.remove-rule') }
		
		smartGroupNameField { $('input', type:'text', name:'name') }
		
		addRuleButton { $('.button', text:"Add more rules") }
		finishButton { $('.button', text:'Finish') }
	}
}