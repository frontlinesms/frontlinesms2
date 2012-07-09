package frontlinesms2.contact

import frontlinesms2.*

class PageContactShow extends PageContact {
	static url = 'contact/show'
	static at = {
		title.contains('Contacts')
	}
	/**
	static content = {
		bodyMenu { $('#body-menu') }
		selectedMenuItem { bodyMenu.find('.selected') }
		groupSubmenu(required:false) { bodyMenu.find('.groups .submenu') }
		groupList(required:false) { $("#group-list") }
		contactSelect(required:false) { $(".contact-select") }	
		multiGroupSelect(required:false) { $('#multi-group-dropdown') }
		updateAll(required:false) { $("#update-all") }
		flashMessage(required:false) { $('div.flash') }

		frmDetails { $("#details") }
		btnSave { frmDetails.find('#update-single') }
		btnCancel { $(".buttons .cancel") }
		deleteSingleButton { $('#btn_delete') }
		deleteAllButton { $('#btn_delete_all') }
		contactCount { $('#contact-count') }
		searchBtn { $('#message-search .buttons') }
		
		// Popup
		confirmDeleteButton(required:false) { $("#done") }
		
		// When showing a group
		contactsList(required:false) { $('#contact-list') }
		
		// Groups
		
		moreGroupActions(required:false) { $('.section-header #group-actions') }
		
		// Custom Fields
		fieldSelecter { $('#new-field-dropdown') }
	}
	**/
}
