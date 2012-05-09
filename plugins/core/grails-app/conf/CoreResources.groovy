modules = {
	common {
		dependsOn "jquery, jquery-ui"
		resource url: [dir:'js', file:"application.js"], disposition: 'head'
		resource url: "/css/colors.css"
		resource url: [dir:'css', file:"screen.css"]
		resource url: [dir:'css', file:"buttons.css"]
		resource url: [dir:'css', file:"header.css"]
		resource url: [dir:'css', file:"help.css"]
		resource url: [dir:'css', file:"print.css"]
		resource url: [dir:'css', file:"help.css"]
		resource url: [dir:'js', file:"activity/popups.js"], disposition: 'head'
		resource url: [dir:'js', file:"mediumPopup.js"], disposition: 'head'
		resource url: [dir:'js', file:"smallPopup.js"], disposition: 'head'
		resource url: [dir:'js', file:"pagination.js"], disposition: 'head'
		resource url: [dir:'js', file:"jquery.ui.selectmenu.js"], disposition: 'head'
		resource url: [dir:'js', file:'characterSMS-count.js'], disposition: 'head'
		resource url: [dir:'js', file:'check_li.js'], disposition: 'head'
	}
	
	messages {
		dependsOn "jquery, jquery-ui, common"
		resource url: [dir:'css', file:"messages.css"]
		resource url: [dir:'js', file:"message/arrow_navigation.js"], disposition: 'head'
		resource url: [dir:'js', file:"message/star_message.js"], disposition: 'head'
		resource url: [dir:'js', file:"message/categorize_dropdown.js"], disposition: 'head'
		resource url: [dir:'js', file:"message/move_dropdown.js"], disposition: 'head'
		resource url: [dir:'js', file:"message/moreActions.js"], disposition: 'head'
	}
	
	newMessagesCount {
		dependsOn "jquery"
		resource url: [dir:'js', file:"message/check_for_new_messages.js"]
	}
	
	archive {
		dependsOn "jquery, jquery-ui, common, messages"
	}
	
	contacts {
		dependsOn "jquery, jquery-ui, common"
		resource url: [dir:'css', file:"contacts.css"]
		resource url: [dir:'js', file:"contact/validateNumber.js"]
		resource url: [dir:'js', file:"contact/buttonStates.js"]
		resource url: [dir:'js', file:"contact/moreGroupActions.js"]
		resource url: [dir:'js', file:"contact/search_within_list.js"]
		resource url: [dir:'js', file:"contact/show-groups.js"]
		resource url: [dir:'js', file:"contact/show-fields.js"]
	}

	status {
		dependsOn "jquery, jquery-ui, common"
		resource url: [dir:'css', file:"status.css"]
	}

	graph {
		resource url: [dir:'js', file:'/graph/raphael-min.js']
		resource url: [dir:'js', file:'/graph/g.raphael-min.js']
		resource url: [dir:'js', file:'/graph/g.bar-min.js']
		resource url: [dir:'js', file:'/graph/graph.js']
	}

	search {
		dependsOn "jquery, jquery-ui, common, messages"
		resource url: [dir:'css', file:"search.css"]
		resource url: [dir:'js', file:"search/moreOptions.js"]
	}
	
	settings {
		dependsOn "jquery, jquery-ui, common"
		resource url: [dir:'css', file:"settings.css"]
	}
	
	overrides {
		'jquery-theme' {
			resource id: 'theme', url: [dir:'jquery-ui', file:"themes/medium/jquery-ui-1.8.11.custom.css"]
		}
	}
}

