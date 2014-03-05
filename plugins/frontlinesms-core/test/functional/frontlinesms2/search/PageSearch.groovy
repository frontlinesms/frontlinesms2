package frontlinesms2.search

import frontlinesms2.message.*

abstract class PageSearch extends frontlinesms2.page.PageBase {
	static content = {
		searchsidebar { module SearchSideBar }
		detailsidebar { module DetailSideBar }
		bodyMenu { module BodyMenu }
		header { module ContentHeader }
		footer { module ContentFooter }
		messageListHeader { module MessageListHeader }
		messageList { module MessageList }
		singleMessageDetails { module SingleMessageDetails }
		multipleMessageDetails { module MultipleMessageDetails }
	}
}

class SearchSideBar extends geb.Module {
	static base = { $('#body-menu') }
	static content = {
		archive { $('input[name=inArchive]') }
		inArchive { archive.value() }
		activityId { $("#activityId") }
		searchField { $("#searchString") }
		searchString { $("#searchString").jquery.val() }
		searchForm { $('#search-details') }
		searchBtn { $('input', class:'btn search') }
		searchMoreOptionLink { $('#toggle-extra-options')}
		addTownCustomFieldLink(required:false) { $('#town-add')}
		townCustomField(required:false) { $('#town-list-item')}
		clearSearchLink { $('a', class:'btn clear') }
		datePickerDiv { $("#datePicker") }
		likeCustomFieldLink(required:false) { $('#like-add')}
		ikCustomFieldLink(required:false) { $('#ik-add')}
		contactNameLink(required:false) {$('#contactString-add')}
		contactNameField(required:false) {$('#contactString-list-item')}
		expandedSearchOption(required:false) {$('#extra-options-list')}
	}
}

class DetailSideBar extends geb.Module {
	static base = { $('#detail') }
	static content = {
		messageOwnerLink { $('#interaction-detail-owner a')}
		messageContent { $('#interaction-detail-content p').jquery.text() }
	}
}

