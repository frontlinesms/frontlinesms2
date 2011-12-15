package frontlinesms2.search

class PageSearch extends geb.Page {
	static url = 'search'
	static at = {
		title.startsWith('Search')
	}
	static content = {
		searchFrm { $('#search-details') }
		searchBtn { $('input', class:'btn create') }
		searchDescription { $('#activity-details') }
		searchMoreOptionLink { $('#toggle-extra-options')}
		addTownCustomFieldLink(required:false) { $('#town-add')}
		townCustomField(required:false) { $('#town-list-item')}
		likeCustomFieldLink(required:false) { $('#like-add')}
		ikCustomFieldLink(required:false) { $('#ik-add')}
		contactNameLink(required:false) {$('#contactString-add')}
		contactNameField(required:false) {$('#contactString-list-item')}
		expandedSearchOption(required:false) {$('#extra-options-list')}
	}
}