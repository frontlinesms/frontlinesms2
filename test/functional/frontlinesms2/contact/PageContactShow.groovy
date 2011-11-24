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

		frmDetails { $("#details") }
		btnSave { frmDetails.find('#update-single') }
		btnCancel { $(".buttons .cancel") }
		deleteSingleButton { $('#btn_delete') }
		deleteAllButton { $('#btn_delete_all') }
		contactCount { $('#contact-count') }
		searchBtn { $('#message-search .buttons') }
		
		//Popup
		confirmDeleteButton(required:false) { $("#done") }
		
		//When showing a group
		contactsList(required:false) { $('#contact-list') }
		
		//> SMART GROUPS
		smartGroupsList { $('#smart-groups-submenu') }
		smartGroupsListItems {
			def list = smartGroupsList.find('li')
			assert list[-1].@id == 'create-smart-group'
			list = list[0..-2] // remove 'create new smart group' item from list
			if(list.size()==1 && list[0].@id == 'no-smart-groups') {
				return []
			} else return list
		}
		noSmartGroupsMessage(required:false) { smartGroupsList.find('li#no-smart-groups') }
		createSmartGroupButton { $('li#create-smart-group a') }
	}
}
