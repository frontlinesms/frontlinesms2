package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class PageMessageWebConnection extends frontlinesms2.page.PageMessageActivity {
	static content = {
		header { module WebConnectionHeaderModule }
	}
	static at = {
		title.contains("web connection")
	}
}

class WebConnectionHeaderModule extends ContentHeader {
	static content = {
		infoListItems { $('ul.info li')*.text() }
		name { $('ul.info h1').text().toLowerCase() }
		url { $("span#web_connection_url").text().toLowerCase() }
		sendMethod { $("span#web_connection_method").text().toLowerCase() - ')' -'('}
		archive { buttons.filter(text: iContains('Archive')) }
		moreActions { $('div.header-buttons select#more-actions') }
	}
}