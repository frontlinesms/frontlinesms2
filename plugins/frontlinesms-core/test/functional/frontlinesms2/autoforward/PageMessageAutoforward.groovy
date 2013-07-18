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
		message { infoListItems[1] }
		keywords { infoListItems[2] }
		recipients { infoListItems[3] }
	}
}

