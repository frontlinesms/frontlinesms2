package frontlinesms2.poll

import frontlinesms2.*

class PageMessagePoll extends frontlinesms2.page.PageMessageActivity {
	static at = {
		pollGraphBtn.displayed
	}

	static content = {
		moreActions { $('div.header-buttons select#more-actions') }
		categoriseSingle { responseId ->
			$("#single-interaction #categorise_dropdown").jquery.val("btn-"+responseId)
			$("#single-interaction #categorise_dropdown").jquery.trigger("change")
		}
		categoriseMultiple { responseId ->
			$("#multiple-interactions #categorise_dropdown").jquery.val("btn-"+responseId)
			$("#multiple-interactions #categorise_dropdown").jquery.trigger("change")
		}
		statsLabels {$('div.stats table tbody tr td.value')*.text()}
		statsNumbers {$('div.stats table tbody tr td.count')*.text()}
		statsPercents {$('div.stats table tbody tr td.percent')*.text()}
		pollGraphBtn {$("#poll-graph-btn")}
		pollGraph {$('#pollGraph')}
	}

}
