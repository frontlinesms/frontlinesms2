package frontlinesms2.search

class PageSearch extends geb.Page {
	static url = 'search/no_search'
	static at = {
		title.startsWith('Search')
	}
	static content = {
		searchFrm { $('#search-details') }
		searchBtn { $('input', class:'btn search') }
		searchDescription { $('p', class:'description') }
		searchMoreOptionLink { $('#toggle-extra-options')}
		addTownCustomFieldLink(required:false) { $('#town-add')}
		townCustomField(required:false) { $('#town-list-item')}
		likeCustomFieldLink(required:false) { $('#like-add')}
		ikCustomFieldLink(required:false) { $('#ik-add')}
		contactNameLink(required:false) {$('#contactString-add')}
		contactNameField(required:false) {$('#contactString-list-item')}
		expandedSearchOption(required:false) {$('#extra-options-list')}
		clearSearchLink { $('a', class:'btn clear') }
		messageTextLink { $("#main-list tr .message-text-cell a") }
		noMessagesInboxed { $("#inbox-indicator") }
		messageTextInTable { $("#main-list tr td:nth-child(4)") }
		flashMessage { $('.flash') }
		deleteMessageBtn { $("#delete-msg") }
		datePickerDiv { $("#ui-datepicker-div") }
		archiveMsgBtn { $("#archive-msg") }
		singleMessageDetailContent { $("#message-detail-content") }
		exportBtnLink { $('#export-btn a') }
		messageSenderDetail { $("#message-detail #message-detail-sender") }
		multipleMessageDetails { $('#multiple-messages') }
		forwardBtn { $('#btn_forward') }
		messageTextArea { $('textArea', name:'messageText') }
		pageTitle { $('title') }
		tab1 { $("div#tabs-1") }
	}
}
