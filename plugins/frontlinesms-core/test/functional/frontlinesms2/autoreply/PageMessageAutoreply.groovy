package frontlinesms2.autoreply

import frontlinesms2.*

class PageMessageAutoreply extends frontlinesms2.page.PageMessageActivity {
	static content = {
		header { module AutoreplyHeaderModule }
	}
	static at = {
		title.contains("autoreply")
	}
}

class AutoreplyHeaderModule extends frontlinesms2.message.ContentHeader {
	static content = {
		infoListItems { $('ul.info li')*.text() }
		date { infoListItems[0] }
		autoreplyMessage { infoListItems[1] }
	}
}
