package frontlinesms2.contact

import frontlinesms2.*

class PageContactShow extends geb.Page {
	static url = 'contact/show'
	static at = {
		title.endsWith('Contacts')
	}
	static content = {
		selectedMenuItem { $('#contacts-menu .selected') }
		groupsList(required:false) { $('#groups-submenu') }
		contactSelect(required:false) { $(".contact-select") }	
		multiGroupSelect(required:false) { $('#multi-group-dropdown') }
		updateAll(required:false) { $("#update-all") }
		flashMessage(required:false) { $('div.flash') }
		
		//> SMART GROUPS
		smartGroupsList { $('ol#smart-groups-submenu') }
		smartGroupsListItems {
			def list = smartGroupsList.find('li')
			list.size() <= 2? []: list[0..-3]
		}
		noSmartGroupsMessage(required:false) { smartGroupsList.find('li#no-smart-groups') }
		createSmartGroupButton { $('li#create-smart-group a') }
	}
}
