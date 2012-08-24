package frontlinesms2.externalcommand

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class PageMessageExternalCommand extends frontlinesms2.page.PageMessageActivity {
	static content = {
		header { module ExternalCommandHeaderModule }
	}
	static at = {
		title.contains("command")
	}
}

class ExternalCommandHeaderModule extends ContentHeader {
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