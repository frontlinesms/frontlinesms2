package frontlinesms2.page

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
	static base = { $('#body-menu') }
	static content = {
		selectedMenuItem { $('.selected a').text().toLowerCase() }
		groupSubmenuLinks { $('li.groups ul.submenu li a')*.text() }
		getGroupLink { groupName ->
			$('li.groups ul.submenu li a', text:groupName ) 
		}
		newContact { $('li.contacts .create a') }
		newGroup { $('li.groups .create a') }
		smartgroups { $('li.smartgroups')}
		smartGroupSubmenuLinks(required:false) { smartgroups.find('a:not(.create)') }
		createSmartGroupButton { $('li.smartgroups li.create a') }
		getSmartGroupLink { groupName ->
			$('li.smartgroups ul.submenu li a', text:groupName)
		}
		smartGroupIsDisplayed { smartGroupInstance ->
			$("title").text().contains(smartGroupInstance.name)
		}
	}
}

class ContentHeader extends geb.Module {
	static base = { $('#main-list-head') }
	static content = {
		title { $('h1').text().toLowerCase() }
		button { $('a.btn, input[type="button"], button') }
		groupHeaderSection { $('div.group') }
		groupHeaderTitle { $('div.group h1') }
		moreGroupActions { $('div.header-buttons #group-actions') }
	}
}

class ContentFooter extends geb.Module {
	static base = { $('#main-list-foot') }
	static content = {
		search { $('a')[0] }
		searchContact { $('#contact-search')}
		searchDetailsText { $('#contact-search').text().toLowerCase() }
		nextPage { $('#paging a.nextLink') }
		prevPage { $('#paging a.prevLink') }
		currentStep { $('#paging currentStep') }
	}
}

class ContactList extends geb.Module {
	static base = { $('#main-list') }
	static content = {
		contact { $("li a")}
		contacts { $("li a")*.text() }
		contactsLink { $("li a")*.@href }
		selectContact { contactPosition ->
			$('.contact-select', contactPosition).click()
	    }
		selectedContacts { $(".selected li a")*.text() }
		noContent { $('p.no-content').text() }
	}
}

class SingleContactDetails extends geb.Module {
	static base = { $('#single-contact') }
	static content = {
		name { $('#name') }
		mobile { $('#mobile') }
		email { $('#email') }
		notes { $('#notes') }
		customLabel { customField ->
			$('label', text:customField) 
		}
		labels { fieldName ->
			$('label', for:fieldName)   	
		}
		textField { fieldName ->
			$("#$fieldName")
		}
		addMoreInfomation {
			$('select#new-field-dropdown').jquery.val('add-new') 
			$('select#new-field-dropdown').jquery.trigger("change") 
		}
		addCustomField { customFieldValue ->
			$('select#new-field-dropdown').jquery.val(customFieldValue) 
			$('select#new-field-dropdown').jquery.trigger("change") 
		}
		customField { customFieldName ->
			$ ('input', name:customFieldName)
		}
		customFields { $('select#new-field-dropdown option')*.value() }

		removeCustomFeild { feildId ->
			$('a#remove-field-'+feildId).click()
		}
		contactsCustomFields { $('label', for: startsWith("custom-field-"))*.text() }
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
