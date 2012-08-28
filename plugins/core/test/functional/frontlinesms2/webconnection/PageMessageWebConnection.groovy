package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class PageMessageWebConnection extends frontlinesms2.page.PageMessageActivity {
	static content = {
		header { module WebConnectionHeaderModule }
	}
	static at = {
		title.contains("command")
	}
}

class WebConnectionHeaderModule extends ContentHeader {
	static content = {
		infoListItems { $('ul.info li')*.text() }
		name { infoListItems[0] }
		keyword { infoListItems[1] }
		url { infoListItems[2] }
		sendMethod { infoListItems[3] }
		archive { buttons.filter(text:'Archive subscription') }
		moreActions { $('div.header-buttons select#more-actions') }
	}
}