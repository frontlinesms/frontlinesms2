package frontlinesms2.smartgroup

class SmartGroupCreateDialog extends frontlinesms2.contact.PageContact {
	static at = {
		$("#ui-dialog-title-modalBox").text().equalsIgnoreCase('Create smart group')
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
		editButton { $('button', text:'Edit')}
		
		errorMessages(required:false) { $('.error-panel') }
		flashMessage(required:false) { $('div.flash') }
		// SMART GROUPS
		smartGroupsList { $('#smart-groups-submenu') }
		smartGroupsListItems {
			def list = smartGroupsList.find('li')
			assert list[-1].@id == 'create-smart-group'
			list = list[0..-2] // remove 'create new smart group' item from list
			if(list.size()==1 && list[0].@id == 'no-smart-groups') {
				return []
			} else return list
		}
		createSmartGroupButton { $('li#create-smart-group a') }
		
	}


}
