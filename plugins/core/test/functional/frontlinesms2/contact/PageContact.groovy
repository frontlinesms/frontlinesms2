package frontlinesms2.contact

import frontlinesms2.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

abstract class PageContact extends frontlinesms2.page.PageBase {
	static url = 'contact/'
	static content = {
		bodyMenu { module BodyMenu }
		header { module ContentHeader }
		footer { module ContentFooter }
		contactList { module ContactList }
		singleContactDetails { module SingleContactDetails }
		multipleContactDetails { module MultipleContactDetails }
	}
}

class BodyMenu extends geb.Module {
	static content = {
		bodyMenu { $('#body-menu') }
		selectedMenuItem { bodyMenu.find('.selected').text().toLowerCase() }
		groupSubmenuLinks { bodyMenu.find('li.groups ul.submenu li a') }
		smartGroupSubmenuLinks { bodyMenu.find('li.smartgroups ul.submenu li a') }
		newContact { bodyMenu.find('li.contacts .create a') }
		newGroup { bodyMenu.find('li.groups .create a') }
		newSmartGroup { bodyMenu.find('li.smartgroups .create a') }
	}
}

class ContentHeader extends geb.Module {
	static base = { $('#main-list-head') }
	static content = {
		title { $('h1').text().toLowerCase() }
		button { $('a.btn, input[type="button"], button') }
		moreGroupActions(required:false) { $('div.header-buttons #group-actions-menu') }
	}
}

class ContentFooter extends geb.Module {
	static base = { $('#main-list-foot') }
	static content = {
		search { $('a')[0] }
		noneSearch { $('#contact-search').text().toLowerCase() == "search" }
		searchDetails { $('#contact-search').text() }
		nextPage { $('#paging a.nextLink') }
		prevPage { $('#paging a.prevLink') }
		currentStep { $('#paging currentStep') }
	}
}

class ContactList extends geb.Module {
	static base = { $('#main-list') }
	static content = {
		contacts { $("ul#main-list li a")*.text() }
		selectContact { contactPosition ->
			$('.contact-select', contactPosition).click()
	    }
		selectedContacts { $("ul#main-list.selected li a")*.text() }
		noContent { $('p.no-content') }
	}
}

class SingleContactDetails extends geb.Module {
	static base = { $('#single-contact') }
	static content = {
		name { $('#name') }
		mobile { $('#mobile') }
		email { $('#email') }
		notes { $('#notes')}
		addMoreInfomation { customField -> 
			$('select#new-field-dropdown').jquery.val(customField)
			$('select#new-field-dropdown').jquery.trigger("change")
		}
		groupDropDown { $('#group-dropdown') }
        groupList { $('ul#group-list li span')*.text() }
        removeGroup { groupId ->
			$("#group-list a#remove-group-${groupId}").click()
		}
		removeMobile { $('#remove-mobile') }
		sendMessage { $('#single-contact .send-message') }
        otherGroupOptions { $('#group-dropdown option')*.text().sort() }
		addToGroup { groupId -> 
			$('select#group-dropdown').jquery.val(groupId)
			$('select#group-dropdown').jquery.trigger("change")
		}
		save { $('#action-buttons #update-single') }
		cancel { $('#action-buttons a.cancel', text:'Cancel') }
		delete { $('#btn_delete') }
	}
}

class MultipleContactDetails extends geb.Module {
	static base = { $('#multiple-contacts') }
	static content = {

		checkedContactCount { $("h2#checked-contact-count").text().split(" ")[0].toInteger() }
	
		multiGroupDropDown { $('#multi-group-dropdown') }
		multiGroupList { $('ul#multi-group-list li span')*.text() }
		otherMultiGroupOptions { $('#multi-group-dropdown option')*.text().sort() }
		addToGroup { groupId -> 
			$('select#multi-group-dropdown').jquery.val(groupId)
			$('select#multi-group-dropdown').jquery.trigger("change")
		}
		removeMultiGroup { groupId ->
		    $("#multi-group-list a#remove-group-${groupId}").click()
		}
		update { $('#action-buttons #update-all') }
		delete { $('#action-buttons #btn_delete_all') }

		checkedContactCount(required:false) { $("h2#checked-contact-count").text().split(" ")[0].toInteger() }
		deleteAllButton(required:false) { $('#btn_delete_all') }

	}
}
