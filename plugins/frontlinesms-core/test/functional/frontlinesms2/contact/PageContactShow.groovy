package frontlinesms2.contact

import frontlinesms2.*

class PageContactShow extends frontlinesms2.page.PageContact {
	static url = ''
	String convertToPath(Object [] args) {
		if(!args) {
			return "contact/show"
		} else if(args[0] instanceof String) {
			def contactId = remote { Contact.findByName(args[0]).id }
			return "contact/show/${contactId}"
		} else if(args[0] instanceof Number) {
			return "contact/show/${args[0]}"
		}
	}

	static at = {
		title == 'contact.header'
	}
}

class PageGroupShow extends PageContactShow {
	String convertToPath(Object [] args) {
		if(!args) {
			return "contact/show"
		} else if(args[0] instanceof Number) {
			return "group/show/${(args[0])}"
		} else if(args[0] instanceof String) {
			def id = remote { Group.findByName(args[0]).id }
			return "group/show/$id"
		}
	}

	static at = {
		title ==~ /contact.header.group\[.*\]/
	}
}

