package frontlinesms2.subscription

import frontlinesms2.*
import frontlinesms2.message.*

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
		groupLink { buttons.find(text:'Go to group') }
	}
}
