package frontlinesms2.subscription

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class PageMessageSubscription extends frontlinesms2.page.PageMessageActivity {
	static content = {
		header { module SubscriptionHeaderModule }
	}
}

class SubscriptionHeaderModule extends ContentHeader {
	static content = {
		infoListItems { $('ul.info li')*.text() }
		group { infoListItems[0] }
		groupMemberCount { infoListItems[1] }
		keyword { infoListItems[2] }
		joinAliases { infoListItems[3] }
		leaveAliases { infoListItems[4] }
		groupLink { buttons.filter(text:'View Group') }
		archive { buttons.filter(text:'Archive subscription') }
		moreActions { $('div.header-buttons select#more-actions') }
	}
}
