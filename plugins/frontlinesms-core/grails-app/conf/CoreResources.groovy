modules = {
	common { dependsOn 'frontlinesms-core' }

	'frontlinesms-core' {
		dependsOn 'jquery-ui'
		dependsOn 'flags'
		resource url:[dir:'css', file:'reset.css']
		resource url:[dir:'css', file:'layout.css']
		resource url:[dir:'css', file:'head.css']
		resource url:[dir:'css', file:'controls.css']
		resource url:[dir:'css', file:'message.css']
		resource url:[dir:'css', file:'contact.css']
		resource url:[dir:'css', file:'archive.css']
		resource url:[dir:'css', file:'activity.css']
		resource url:[dir:'css', file:'activity/customactivity.css']
		resource url:[dir:'css', file:'activity/webconnection.css']
		resource url:[dir:'css', file:'search.css']
		resource url:[dir:'css', file:'settings.css']
		resource url:[dir:'css', file:'status.css']
		resource url:[dir:'css', file:'wizard.css']
		resource url:[dir:'css', file:'chosen.css']
		resource url:[dir:'css', file:'color.css']
		resource url:[dir:'css', file:'unreviewed-core.css']

		resource url:[dir:'js/layout', file:'resizer.js'], disposition:'head'
		resource url:[dir:'css', file:'status.css']

		resource url:[dir:'css', file:'help.css']
		resource url:[dir:'js', file:'frontlinesms_core.js'], disposition:'head'
		resource url:[dir:'js', file:'timer.js'], disposition:'head'
		resource url:[dir:'js', file:'app_info.js'], disposition:'head'
		resource url:[dir:'js', file:'activity/popups.js'], disposition:'head'
		resource url:[dir:'js', file:"activity/custom_activity.js"], disposition:'head'
		resource url:[dir:'js', file:'activity/popupCustomValidation.js'], disposition:'head'
		resource url:[dir:'js', file:'activity/poll/poll.js'], disposition:'head'
		resource url:[dir:'js', file:'activity/poll/poll_graph.js'], disposition:'head'
		resource url:[dir:'js', file:'activity/webconnection.js'], disposition:'head'
		resource url:[dir:'js', file:'activity/subscription.js'], disposition:'head'
		resource url:[dir:'js', file:'button.js'], disposition:'head'
		resource url:[dir:'js', file:'characterSMS-count.js'], disposition:'head'
		resource url:[dir:'js', file:'check_list.js'], disposition:'head'
		resource url:[dir:'js', file:'fconnection.js'], disposition:'head'
		resource url:[dir:'js', file:'routing.js'], disposition:'head'
		resource url:[dir:'js', file:'jquery.ui.selectmenu.js'], disposition:'head'
		resource url:[dir:'js', file:'jquery.validate.min.js'], disposition:'head'
		resource url:[dir:'js', file:'mediumPopup.js'], disposition:'head'
		resource url:[dir:'js', file:'new_features.js'], disposition:'head'
		resource url:[dir:'js', file:'pagination.js'], disposition:'head'
		resource url:[dir:'js', file:'recipient_selecter.js'], disposition:'head'
		resource url:[dir:'js', file:'sanchez.min.js'], disposition:'head'
		resource url:[dir:'js', file:'settings/connectionTooltips.js'], disposition:'head'
		resource url:[dir:'js', file:'smallPopup.js'], disposition:'head'
		resource url:[dir:'js', file:'status_indicator.js'], disposition:'head'
		resource url:[dir:'js', file:'system_notification.js'], disposition:'head'
		resource url:[dir:'js', file:'magicwand.js'], disposition:'head'
		resource url:[dir:'js', file:'contactsearch.js'], disposition:'head'
		resource url:[dir:'js', file:'chosen.jquery.js'], disposition:'head'
		resource url:[dir:'js', file:'ajax-chosen.js'], disposition:'head'
		resource url:[dir:'js', file:'selectmenuTools.js'], disposition:'head'
		resource url:[dir:'js', file:'jquery.autosize-min.js']
		resource url:[dir:'js', file:'message_composer.js']
		resource url:[dir:'js', file:'frontlinesync.js', disposition:'head']
		resource url:[dir:'js', file:'inline_editable.js', disposition:'head']
	}
	
	messages {
		dependsOn 'common, font-awesome'
		resource url:[dir:'js', file:'message/arrow_navigation.js'], disposition:'head'
		resource url:[dir:'js', file:'message/star_message.js'], disposition:'head'
		resource url:[dir:'js', file:'message/categorize_dropdown.js'], disposition:'head'
		resource url:[dir:'js', file:'message/move_dropdown.js'], disposition:'head'
		resource url:[dir:'js', file:'message/moreActions.js'], disposition:'head'
		resource url:[dir:'js', file:'message/check_for_new_messages.js']
		resource url:[dir:'js', file:'message/new_message_summary.js']
		resource url:[dir:'js', file:'jquery.pulse.js']
	}

	archive {
		dependsOn 'messages'
	}
	
	contacts {
		dependsOn 'common, font-awesome'
		resource url:[dir:'js', file:'contact/moreGroupActions.js']
		resource url:[dir:'js', file:'contact/search_within_list.js']
		resource url:[dir:'js', file:'contact/editor.js']
		resource url:[dir:'js', file:'contact/groups-editor.js']
		resource url:[dir:'js', file:'contact/validateContact.js']
	}

	status {
		dependsOn 'common'
		resource url:[dir:'js', file:'datepicker.js']
	}

	graph {
		resource url:[dir:'js', file:'/graph/graph-utils.js']
		resource url:[dir:'js', file:'/graph/jquery.jqplot.min.js']
		resource url:[dir:'js', file:'/graph/jqplot.barRenderer.min.js']
		resource url:[dir:'js', file:'/graph/jqplot.categoryAxisRenderer.min.js']
		resource url:[dir:'js', file:'/graph/jqplot.pointLabels.min.js']
		resource url:[dir:'js', file:'/graph/jqplot.highlighter.min.js']
		resource url:[dir:'js', file:'/graph/jqplot.enhancedLegendRenderer.min.js']
		resource url:[dir:'css', file:'jquery.jqplot.css']
	}

	search {
		dependsOn 'messages'
		resource url:[dir:'js', file:'datepicker.js']
		resource url:[dir:'js', file:'search/moreOptions.js']
		resource url:[dir:'js', file:'search/basicFilters.js']
	}
	
	settings {
		dependsOn 'common, font-awesome'
		resource url:[dir:'js', file:'/settings/general_settings.js']
		resource url:[dir:'js', file:'jquery.pulse.js']
		resource url:[dir:'js', file:'contact/import_review.js']
	}
	
	overrides {
		'jquery-theme' {
			dependsOn 'jquery-ui-base-imports'
			resource id:'theme', url:[dir:'jquery-ui', file:'themes/medium/jquery-ui.custom.css'], bundle:'frontlinesms-core'
		}
	}

	'jquery-ui-base-imports' {
		resource url:[dir:'jquery-ui/themes/medium', file:'jquery.ui.core.css'], bundle:'frontlinesms-core'
		resource url:[dir:'jquery-ui/themes/medium', file:'jquery.ui.autocomplete.css'], bundle:'frontlinesms-core'
		resource url:[dir:'jquery-ui/themes/medium', file:'jquery.ui.datepicker.css'], bundle:'frontlinesms-core'
		resource url:[dir:'jquery-ui/themes/medium', file:'jquery.ui.dialog.css'], bundle:'frontlinesms-core'
		resource url:[dir:'jquery-ui/themes/medium', file:'jquery.ui.progressbar.css'], bundle:'frontlinesms-core'
		resource url:[dir:'jquery-ui/themes/medium', file:'jquery.ui.resizable.css'], bundle:'frontlinesms-core'
		resource url:[dir:'jquery-ui/themes/medium', file:'jquery.ui.selectable.css'], bundle:'frontlinesms-core'
		resource url:[dir:'jquery-ui/themes/medium', file:'jquery.ui.selectmenu.css'], bundle:'frontlinesms-core'
		resource url:[dir:'jquery-ui/themes/medium', file:'jquery.ui.slider.css'], bundle:'frontlinesms-core'
		resource url:[dir:'jquery-ui/themes/medium', file:'jquery.ui.tabs.css'], bundle:'frontlinesms-core'
	}

	'internet-explorer-css' {
		resource url:[dir:'css', file:'ie7.css'], bundle:'ie7'
		resource url:[dir:'css', file:'ie8.css'], bundle:'ie8'
	}

	flags {
		resource url:[dir:'css', file:'flags.css']
	}
}

