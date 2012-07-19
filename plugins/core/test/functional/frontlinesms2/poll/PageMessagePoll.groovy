package frontlinesms2.poll

import frontlinesms2.*

class PageMessagePoll extends frontlinesms2.page.PageMessageActivity {
	static url = 'message/activity'
	static content = {
		moreActions { $('div.header-buttons select#more-actions') }
		categoriseSingle { responseId ->
			$("#single-message #categorise_dropdown").jquery.val(responseId)
			$("#single-message #categorise_dropdown").jquery.trigger("change")
		}
		categoriseMultiple { responseId ->
			$("#multiple-messages #categorise_dropdown").jquery.val(responseId)
			$("#multiple-messages #categorise_dropdown").jquery.trigger("change")
		}
	}

}