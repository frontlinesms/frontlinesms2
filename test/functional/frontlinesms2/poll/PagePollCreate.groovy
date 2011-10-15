package frontlinesms2.poll

import frontlinesms2.*

class PagePollCreate extends geb.Page {
	static at = { 
		$("#ui-dialog-title-modalBox").text() == "New Poll"
	}
	static content = {
		tabMenu { $("#tabs li") }
		
		enterQuestionTab { $("#tabs-1") }
		responseListTab { $("#tabs-2") }
		responseListTabLink { tabMenu[1] }
		autoSortTab { $("#tabs-3") }
		autoReplyTab { $("#tabs-4") }
		editMessageTab { $("#tabs-5") }
		selectRecipientsTab { $("#tabs-6") }
		selectRecipientsTabLink { tabMenu[4] }
		confirmationTab { $("#tabs-6") }
		
		next { $("#nextPage") }
		prev { $("#prevPage") }
		done { $("#done") }
		
		pollForm { $('form', name:'poll-details') }

		choiceALabel { $('label', for:'choiceA') }
		choiceBLabel { $('label', for:'choiceB') }
		choiceCLabel { $('label', for:'choiceC') }
		choiceDLabel { $('label', for:'choiceD') }
		choiceELabel { $('label', for:'choiceE') }
		
		addManualAddress { $('.add-address') }
		
		errorMessage(required:false) { $('.error-panel') }
	}
}