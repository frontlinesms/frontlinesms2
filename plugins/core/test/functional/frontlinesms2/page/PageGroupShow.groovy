package frontlinesms2.page

import frontlinesms2.*
import frontlinesms2.contact.PageContactShow

class PageGroupShow extends PageContactShow {
	String convertToPath(Object[] args) {
		if(!(args[0] instanceof Group)) {
			throw new IllegalArgumentException("Must supply a group.")
		}
		return super.convertToPath(args)
	}

	static at = { Group g ->
		title == "Contacts >> $g.name"
	}
}

