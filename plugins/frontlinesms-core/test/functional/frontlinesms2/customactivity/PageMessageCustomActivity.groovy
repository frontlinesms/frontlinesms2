package frontlinesms2.customactivity

import frontlinesms2.*

class PageMessageCustomActivity extends frontlinesms2.page.PageMessageActivity {
	static at = {
		title.contains("customactivity")
	}
	static content = {
		moreActions { $('div.header-buttons select#more-actions') }
	}
}
