package frontlinesms2.search

class PageSearch extends geb.Page {
	static url = 'search'
	static at = {
		title.startsWith('Search')
	}
	static content = {
		searchFrm { $('#search-details') }
		searchBtn { $('#search-details .buttons .search') }
		searchDescription { $('#search-description') }
		searchMoreOptionLink { $('#more-search-options')}
		townCustomFieldLink(required:false) { $('#more-option-link-custom-field-town')}
		townCustomFieldField(required:false) { $('#more-option-field-custom-field-town')}
		likeCustomFieldLink(required:false) { $('#more-option-link-custom-field-like')}
		ikCustomFieldLink(required:false) { $('#more-option-link-custom-field-ik')}
		contactNameLink(required:false) {$('#more-option-link-contact-name')}
		contactNameField(required:false) {$('#more-option-field-contact-name')}
		expendedSearchOption(required:false) {$('#expanded-search-options')}
	}
}