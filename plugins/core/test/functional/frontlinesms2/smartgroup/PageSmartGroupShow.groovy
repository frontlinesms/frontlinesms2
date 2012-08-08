package frontlinesms2.smartgroup

import frontlinesms2.*

class PageSmartGroup extends frontlinesms2.page.PageContact {
	static url = 'contact/'

	static content = {
		contactLink { $('#contact-list a') }
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
			$("a", text:"${g.name}").parent().hasClass('selected')
		}
	}
}