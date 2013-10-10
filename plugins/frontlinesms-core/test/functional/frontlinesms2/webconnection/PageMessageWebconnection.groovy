package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class PageMessageWebconnection extends frontlinesms2.page.PageMessageActivity {
	static content = {
		header { module WebconnectionHeaderModule }
	}
	static at = {
		title ==~ /webconnection.title\[.*\]/
	}
}

class WebconnectionHeaderModule extends ContentHeader {
	static content = {
		infoListItems { $('ul.info li')*.text() }
		name { $('ul.info h1').text()?.toLowerCase() }
		subtitle { $('.info p.subtitle').text()?.toLowerCase() }
		api { $('.info span#api').text()?.toLowerCase() }
		url { $("span#web_connection_url").text()?.toLowerCase() }
		sendMethod { $("span#web_connection_method").text()?.toLowerCase() - ')' -'('}
		archive { buttons.filter(text: iContains('Archive')) }
	}
}

