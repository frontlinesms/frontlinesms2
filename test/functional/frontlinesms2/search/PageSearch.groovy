package frontlinesms2.search

class PageSearch extends geb.Page {
	static url = 'search'
	static at = {
		title.startsWith('Search')
	}
	static content = {
		searchFrm { $('#search-details') }
		searchBtn { $('#search-details .buttons .search') }
		searchDescription { $('#activity-details') }
		searchMoreOptionLink { $('#toggle-extra-options')}
		addTownCustomFieldLink(required:false) { $('#town-add')}
		townCustomFieldField(required:false) { $('#town-list-item')}
		likeCustomFieldLink(required:false) { $('#like-add')}
		ikCustomFieldLink(required:false) { $('#ik-add')}
		contactNameLink(required:false) {$('#contact-name-add')}
		contactNameField(required:false) {$('#contact-name-list-item')}
		expandedSearchOption(required:false) {$('#extra-options-list')}
	}
}