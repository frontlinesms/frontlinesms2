package frontlinesms2.folder

import frontlinesms2.*
import frontlinesms2.message.*

class PageMessageFolder extends PageMessage {
	static url = 'message/folder'

	String convertToPath(Object[] args) {
		if (!args) {
			return ""
		}

		def path = ''
		if (args[0] instanceof Number) {
			path += '/' + args[0]
		}
		if(args.length > 1) {
			path += '/show/' + args[1]
		}
		return path
	}
	static content = {
		folderLinks { $('ul li.folders ul.submenu li a') }
		folderMoreActions { $(".header-buttons #more-actions") }
	}
}

