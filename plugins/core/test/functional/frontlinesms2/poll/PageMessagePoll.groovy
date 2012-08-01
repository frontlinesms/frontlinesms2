package frontlinesms2.poll

import frontlinesms2.*

class PageMessagePoll extends frontlinesms2.page.PageMessageActivity {
	static url = 'message/activity'
	static content = {
		moreActions { $('div.header-buttons select#more-actions') }
		categoriseSingle { responseId ->
			$("#single-message #categorise_dropdown").jquery.val("btn-"+responseId)
			$("#single-message #categorise_dropdown").jquery.trigger("change")
		}
		categoriseMultiple { responseId ->
			$("#multiple-messages #categorise_dropdown").jquery.val("btn-"+responseId)
			$("#multiple-messages #categorise_dropdown").jquery.trigger("change")
		}
		statsLabels {$('#poll-stats tbody tr td:first-child')*.text()}
		statsNums {$('#poll-stats tbody tr td:nth-child(2)')*.text()}
		statsPercents {$('#poll-stats tbody tr td:nth-child(3)')*.text()}
	}

}