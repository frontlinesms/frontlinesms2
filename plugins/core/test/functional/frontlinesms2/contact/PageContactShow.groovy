package frontlinesms2.contact

import frontlinesms2.*

class PageContactShow extends PageContact {
	static url = ''
	String convertToPath(Object [] args) {
		if (args.equals(null) || args.length == 0)
			return "contact/show"
		if (args[0] instanceof Contact)
			return "contact/show/${(args[0] as Contact).id}"
		if (args[0] instanceof Group && args.length == 2)
			return "group/show/${(args[0] as Group).id}/contact/show/${(args[1] as Contact).id}" 
		if (args[0] instanceof Group && args.length == 1)
			return "group/show/${(args[0] as Group).id}" 
	}
}