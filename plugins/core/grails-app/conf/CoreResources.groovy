modules = {
	common {
		dependsOn "jquery, jquery-ui"
		resource url:[dir:'css', file:'reset.css', plugin:'core']
		resource url:[dir:'css', file:'layout.css', plugin:'core']
		resource url:[dir:'css', file:'head.css', plugin:'core']
		resource url:[dir:'css', file:'controls.css', plugin:'core']
		resource url:[dir:'css', file:'message.css', plugin:'core']
		resource url:[dir:'css', file:'contact.css', plugin:'core']
		resource url:[dir:'css', file:'archive.css', plugin:'core']
		resource url:[dir:'css', file:'activity.css', plugin:'core']
		resource url:[dir:'css', file:'search.css', plugin:'core']
		resource url:[dir:'css', file:'settings.css', plugin:'core']
		resource url:[dir:'css', file:'status.css', plugin:'core']
		resource url:[dir:'css', file:'wizard.css', plugin:'core']


		resource url:[dir:'js/layout', file:'resizer.js', plugin:'core'], disposition:'head'
		resource url:[dir:'css', file:'status.css', plugin:'core']

		resource url:[dir:'css', file:'help.css', plugin:'core']

		resource url:[dir:'js', file:"frontlinesms_core.js", plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:"activity/popups.js", plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:'button.js', plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:'characterSMS-count.js', plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:'check_li.js', plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:"jquery.ui.selectmenu.js", plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:"jquery.validate.min.js", plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:"mediumPopup.js", plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:"pagination.js", plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:"smallPopup.js", plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:"status_indicator.js", plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:"system_notification.js", plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:'magicwand.js', plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:'selectmenuTools.js', plugin:'core'], disposition:'head'
	}
	
	messages {
		dependsOn "jquery, jquery-ui, common"
		resource url:[dir:'js', file:"message/arrow_navigation.js", plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:"message/star_message.js", plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:"message/categorize_dropdown.js", plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:"message/move_dropdown.js", plugin:'core'], disposition:'head'
		resource url:[dir:'js', file:"message/moreActions.js", plugin:'core'], disposition:'head'
	}
	
	newMessagesCount {
		dependsOn "jquery"
		resource url:[dir:'js', file:"message/check_for_new_messages.js", plugin:'core']
	}
	
	archive {
		dependsOn "messages"
	}
	
	contacts {
		dependsOn "common"
		resource url:[dir:'js', file:"contact/buttonStates.js", plugin:'core']
		resource url:[dir:'js', file:"contact/moreGroupActions.js", plugin:'core']
		resource url:[dir:'js', file:"contact/search_within_list.js", plugin:'core']
		resource url:[dir:'js', file:"contact/show-groups.js", plugin:'core']
		resource url:[dir:'js', file:"contact/show-fields.js", plugin:'core']
		resource url:[dir:'js', file:"contact/validateNumber.js", plugin:'core']
	}

	status {
		dependsOn "common"
		resource url:[dir:'js', file:"datepicker.js", plugin:'core']
	}

	graph {
		resource url:[dir:'js', file:'/graph/graph-utils.js', plugin:'core']
		resource url:[dir:'js', file:'/graph/jquery.jqplot.min.js', plugin:'core']
		resource url:[dir:'js', file:'/graph/jqplot.barRenderer.min.js', plugin:'core']
		resource url:[dir:'js', file:'/graph/jqplot.categoryAxisRenderer.min.js', plugin:'core']
		resource url:[dir:'js', file:'/graph/jqplot.pointLabels.min.js', plugin:'core']
		resource url:[dir:'js', file:'/graph/jqplot.highlighter.min.js', plugin:'core']
		resource url:[dir:'js', file:'/graph/jqplot.enhancedLegendRenderer.min.js', plugin:'core']
		resource url:[dir:'css', file:"jquery.jqplot.css", plugin:'core']
	}

	search {
		dependsOn "messages"
		resource url:[dir:'js', file:"datepicker.js", plugin:'core']
		resource url:[dir:'js', file:"search/moreOptions.js", plugin:'core']
		resource url:[dir:'js', file:"search/basicFilters.js", plugin:'core']
	}
	
	settings {
		dependsOn "common"
	}
	
	overrides {
		'jquery-theme' {
			resource id: 'theme', url:[dir:'jquery-ui', file:"themes/medium/jquery-ui-1.8.11.custom.css", plugin:'core']
		}
	}

}
