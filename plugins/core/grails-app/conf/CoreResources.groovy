modules = {
	common {
		dependsOn "jquery, jquery-ui"
		resource url: [plugin:'core', dir:'js', file:"application.js"], disposition: "head"
		resource url: [plugin:'core', dir:'css', file:"screen.css"]
		resource url: [plugin:'core', dir:'css', file:"print.css"]
		resource url: [plugin:'core', dir:'js', file:"mediumPopup.js"]
		resource url: [plugin:'core', dir:'js', file:"smallPopup.js"]
		resource url: [plugin:'core', dir:'js', file:"pagination.js"]
		resource url: [plugin:'core', dir:'js', file:"jquery.ui.selectmenu.js"]
	}
	
	messages {
		dependsOn "jquery, jquery-ui, common"
		resource url: [plugin:'core', dir:'js', file:"message/check_message.js"]
		resource url: [plugin:'core', dir:'js', file:"message/arrow_navigation.js"]
		resource url: [plugin:'core', dir:'js', file:"message/star_message.js"]
		resource url: [plugin:'core', dir:'js', file:"message/categorize_dropdown.js"]
		resource url: [plugin:'core', dir:'js', file:"message/move_dropdown.js"]
		resource url: [plugin:'core', dir:'js', file:"message/moreActions.js"]
		resource url: [plugin:'core', dir:'js', file:"message/check_for_new_messages.js"]
	}
	
	archive {
		dependsOn "jquery, jquery-ui, common"
		resource url: [plugin:'core', dir:'js', file:"message/check_message.js"]
		resource url: [plugin:'core', dir:'js', file:"message/star_message.js"]
	}
	
	contacts {
		dependsOn "jquery, jquery-ui, common"
		resource url: [plugin:'core', dir:'js', file:"contact/validateNumber.js"]
		resource url: [plugin:'core', dir:'js', file:"contact/buttonStates.js"]
		resource url: [plugin:'core', dir:'js', file:"contact/checked_contact.js"]
		resource url: [plugin:'core', dir:'js', file:"contact/moreGroupActions.js"]
		resource url: [plugin:'core', dir:'js', file:"contact/search_within_list.js"]
		resource url: [plugin:'core', dir:'js', file:"contact/show-groups.js"]
		resource url: [plugin:'core', dir:'js', file:"contact/show-fields.js"]
	}
	
	search {
		dependsOn "jquery, jquery-ui, common"
		resource url: [plugin:'core', dir:'js', file:"search/moreOptions.js"]
		resource url: [plugin:'core', dir:'js', file:"message/check_message.js"]
		resource url: [plugin:'core', dir:'js', file:"message/arrow_navigation.js"]
		resource url: [plugin:'core', dir:'js', file:"message/move_dropdown.js"]
		resource url: [plugin:'core', dir:'js', file:"message/star_message.js"]
		resource url: [plugin:'core', dir:'js', file:"message/moreActions.js"]
	}
	
	overrides {
		'jquery-theme' {
			resource id: 'theme', url: [plugin:'core', dir:'jquery-ui', file:"themes/medium/jquery-ui-1.8.11.custom.css"]
		}
	}
}
