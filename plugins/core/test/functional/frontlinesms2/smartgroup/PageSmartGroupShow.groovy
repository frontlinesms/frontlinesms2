package frontlinesms2.smartgroup

import frontlinesms2.*

class PageSmartGroup extends frontlinesms2.page.PageContact {
	static url = 'contact/'

	static content = {
		smartgroups { $('li.smartgroups')}
		smartGroupSubmenuLinks(required:false) { smartgroups.find('a:not(.create)') }
		createSmartGroupButton { $('li.smartgroups li.create a') }
		smartGroupIsDisplayed { smartGroupInstance ->
			$("title").text().contains(smartGroupInstance.name)
		}
		contactLink { $('#contact-list a') }
		moreActions { $("#group-actions") }
		moreActionsSelect { value ->
			moreActions.value(value).jquery.trigger("click")
		}
		dialogIsDisplayed { $('#ui-dialog-title-modalBox').displayed }
		done { $('#done')}
		inputValue { input, value ->
			$("#${input}").value(value)
		}
	}
}

class PageSmartGroupShow extends PageSmartGroup {
	static url = ''

	String convertToPath(Object [] args) {
		if (args[0] instanceof SmartGroup && args.length == 2)
			return "contact/show/${(args[1] as Contact).id}?smartGroupId=${(args[0] as SmartGroup).id}" 
		if (args[0] instanceof SmartGroup && args.length == 1)
			return "contact/show?smartGroupId=${(args[0] as SmartGroup).id}" 
	}

	static content = {
		menuItemHighlighted { g ->
		$("a#smartgroup-link-${g.id}").parent().hasClass('selected')
	}
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
		removeRuleButtons(required:false) { $('tr.smart-group-criteria a.remove-command') }
		
		smartGroupNameField { $('input', type:'text', name:'smartgroupname') }
		
		addRuleButton { $('.btn', text:"Add another rule") }

		backButton { $('button', text:'Back') }
		cancelButton { $('button', text:'Cancel') }
		finishButton { $('button', text:'Create') }
		editButton { $('button', text:'Edit')}
				
		errorMessages(required:false) { $('.error-panel') }
		flashMessage(required:false) { $('div.flash') }
		
		getMenuLink { smartGroupName ->
			$('li.smartgroups li:not(.create) a', text:smartGroupName)
		}
	}
}

class SmartGroupEditDialog extends SmartGroupCreateDialog {
	static at = { 
		$("#ui-dialog-title-modalBox").text()?.equalsIgnoreCase('Edit Group')
	}
}