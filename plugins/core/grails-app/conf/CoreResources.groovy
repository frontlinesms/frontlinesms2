modules = {
	common {
		dependsOn "jquery, jquery-ui"
		resource url: "/js/application.js", disposition: "head"
		resource url: "/css/screen.css"
		resource url: "/css/print.css"
		resource url: "/js/mediumPopup.js"
		resource url: "/js/smallPopup.js"
		resource url: "/js/pagination.js"
		resource url: "/js/jquery.ui.selectmenu.js"
	}
	
	messages {
		dependsOn "jquery, jquery-ui, common"
		resource url: "/js/message/check_message.js"
		resource url: "/js/message/arrow_navigation.js"
		resource url: "/js/message/star_message.js"
		resource url: "/js/message/categorize_dropdown.js"
		resource url: "/js/message/move_dropdown.js"
		resource url: "/js/message/moreActions.js"
		resource url: "/js/message/check_for_new_messages.js"
	}
	
	archive {
		dependsOn "jquery, jquery-ui, common"
		resource url: "/js/message/check_message.js"
		resource url: "/js/message/star_message.js"
	}
	
	contacts {
		dependsOn "jquery, jquery-ui, common"
		resource url: "/js/contact/validateNumber.js"
		resource url: "/js/contact/buttonStates.js"
		resource url: "/js/contact/checked_contact.js"
		resource url: "/js/contact/moreGroupActions.js"
		resource url: "/js/contact/search_within_list.js"
		resource url: "/js/contact/show-groups.js"
		resource url: "/js/contact/show-fields.js"
	}
	
	search {
		dependsOn "jquery, jquery-ui, common"
		resource url: "/js/search/moreOptions.js"
		resource url: "/js/message/check_message.js"
		resource url: "/js/message/arrow_navigation.js"
		resource url: "/js/message/move_dropdown.js"
		resource url: "/js/message/star_message.js"
		resource url: "/js/message/moreActions.js"
	}
	
	overrides {
		'jquery-theme' {
			resource id: 'theme', url: '/jquery-ui/themes/medium/jquery-ui-1.8.11.custom.css'
		}
	}
}
