package frontlinesms2.smartgroup

import frontlinesms2.*

class PageSmartGroup extends frontlinesms2.page.PageContact {
	static url = ''
}

class PageSmartGroupShow extends PageSmartGroup {
	static url = ''

	String convertToPath(Object [] args) {
		if (args[0] instanceof SmartGroup && args.length == 2)
			return "group/show/${(args[0] as SmartGroup).id}/contact/show/${(args[1] as Contact).id}" 
		if (args[0] instanceof SmartGroup && args.length == 1)
			return "group/show/${(args[0] as SmartGroup).id}" 
	}
}

class SmartGroupCreateDialog extends PageSmartGroupShow {
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
		
		getMenuLink { smartGroupName ->
			$('#smart-groups-submenu li a', text:smartGroupName)
		}
	}
}

class SmartGroupEditDialog extends SmartGroupCreateDialog {
	static at = { 
		$("#ui-dialog-title-modalBox").text()?.equalsIgnoreCase('Edit Group')
	}
}