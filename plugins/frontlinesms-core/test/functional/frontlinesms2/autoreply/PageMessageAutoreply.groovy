package frontlinesms2.autoreply

import frontlinesms2.*

class PageMessageAutoreply extends frontlinesms2.page.PageMessageActivity {

	static content = {
		moreActions { $('div.header-buttons select#more-actions') }		
	}
}
