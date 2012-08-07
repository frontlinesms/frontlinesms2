package frontlinesms2.smartgroup

import frontlinesms2.*

class PageSmartGroup extends frontlinesms2.page.PageContact {
	static url = 'contact/'

	static content = {
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