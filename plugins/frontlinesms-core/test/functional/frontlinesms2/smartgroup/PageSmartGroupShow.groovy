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
		if(args.length == 2) {
			return "contact/show/${(args[1])}?smartGroupId=${(args[0])}"
		}
		if(args.length == 1) {
			return "contact/show?smartGroupId=${(args[0])}"
		}
	}

	static content = {
		menuItemHighlighted { groupName ->
			$("a", text:"${groupName}").parent().hasClass('selected')
		}
	}
}

