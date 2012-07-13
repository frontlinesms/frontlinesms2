package frontlinesms2.contact

import frontlinesms2.*

class PageContactAll extends PageContact {
	static url = ''
	String convertToPath(Object [] args) {
		if (args.equals(null) || args.length == 0)
			return "contact/show"
		if (args[0] instanceof Contact)
			return "contact/show/${(args[0] as Contact).id}"
		if (args[0] instanceof Group)
			return "group/show/${(args[0] as Group).id}/contact/show/${(args[1] as Contact).id}" 
	}
}