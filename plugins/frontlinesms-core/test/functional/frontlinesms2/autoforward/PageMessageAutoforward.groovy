package frontlinesms2.autoforward

import frontlinesms2.*

class PageMessageAutoforward extends frontlinesms2.page.PageMessageActivity {
	static content = {
		header { module AutoforwardHeaderModule }
	}
	static at = {
		title.contains("autoforward")
	}
}

class AutoforwardHeaderModule extends frontlinesms2.message.ContentHeader {
	static content = {
		infoListItems { $('ul.info li')*.text() }
		message { infoListItems[0] }
		keywords { infoListItems[1] }
		recipients { infoListItems[2] }
	}
}
