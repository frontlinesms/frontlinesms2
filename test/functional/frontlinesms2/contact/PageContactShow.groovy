package frontlinesms2.contact

import frontlinesms2.*

class PageContactShow extends geb.Page {
	static url = 'contact/show'
	static at = {
		title.endsWith('Contacts')
	}
	static content = {
		selectedMenuItem { $('#contacts-menu .selected') }
		groupSubmenu(required:false) { $('#groups-submenu') }
		groupList(required:false) { $("#group-list")}
		contactSelect(required:false) { $(".contact-select") }	
		multiGroupSelect(required:false) { $('#multi-group-dropdown') }
		updateAll(required:false) { $("#update-all") }
		flashMessage(required:false) { $('div.flash') }
		frmDetails (required:false){ $("#contact_details") }
		btnSave(required:false) { frmDetails.find('#update-single') }
		btnCancel(required:false) { $(".buttons .cancel")}
		deleteSingleButton(required:false) { $('#btn_delete') }
		deleteAllButton(required:false) { $('#btn_delete_all') }
		contactCount(required:false) {$('#contact-count')}
		searchBtn(required:false){$('#message-search .buttons')}
		
		//Popup
		confirmDeleteButton(required:false) { $("#done") }
		
		//When showing a group
		contactsList(required:false) { $('#contact-list') }
	}
}
