var more_actions = (function() {
	var
	getOwnerId = function() {
		return $("#ownerId").val();
	},
	getController = function() {
		// This is a legacy feature - getController is only
		// used to determine the controller to use, so we should
		// use a specific one when it is available, but otherwise
		// use standard activity.
		return $("#activityType").val() || $("#messageSection").val();
	},
	buildUrl = function(action) {
		return url_root + getController() + "/" + action;
	},
	deleteAction = function() {
		$.ajax({
			type:'GET',
			url: buildUrl("confirmDelete"),
			data: { id:getOwnerId() },
			success: function(data) {
				launchSmallPopup(i18n("smallpopup.fmessage.delete.title", getController()), data, i18n("action.delete")); }
		});
	},
	exportAction = function() {
		var viewingArchive, params;
		viewingArchive = url.indexOf("/archive/") !== -1;
		params = {
				messageSection: $("#messageSection").val(),
				ownerId: getOwnerId(),
				starred: $('input:hidden[name=starred]').val(),
				inbound: $('input:hidden[name=inbound]').val(),
				failed: $('input:hidden[name=failed]').val(),
				viewingArchive: viewingArchive,
				searchString: $("#searchString").val(),
				messageTotal: $("#messageTotal").val(),
				groupId: $("#groupId").val() };

		$.ajax({
			type:'GET',
			url: url_root + 'export/messageWizard',
			data: params,
			beforeSend: function() { showThinking(); },
			success: function(data) {
				hideThinking();
				launchSmallPopup(i18n("smallpopup.fmessage.export.title"), data, i18n("action.export"));
				updateExportInfo();
			}
		});
	},
	editAction = function() {
		var title;
		title = i18n("wizard.fmessage.edit.title", getController());
		$.ajax({
			type:'GET',
			url: buildUrl("edit"),
			data: { id:getOwnerId() },
			beforeSend: function() { showThinking(); },
			success: function(data) {
				hideThinking();
				mediumPopup.launchMediumWizard(title, data, i18n('wizard.ok'), 675, 500, false);
			}
		});
	},
	renameAction = function() {
		$.ajax({
			type:'GET',
			url: buildUrl("rename"),
			data: { ownerId:getOwnerId() },
			beforeSend: function() { showThinking(); },
			success: function(data) {
				hideThinking();
				launchSmallPopup(i18n("smallpopup.fmessage.rename.title", getController()), data, i18n("action.rename"), 'validate');
			}
		});
	},
	init = function() {
		$('.more-actions').bind('change', function() {
			if ($(this).find('option:selected').val() === 'delete') {
				deleteAction();
			} else if($(this).find('option:selected').val() === 'rename') {
				renameAction();
			} else if($(this).find('option:selected').val() === 'edit') {
				editAction();
			} else if($(this).find('option:selected').val() === 'export') {
				exportAction();
			}
			selectmenuTools.snapback($('#more-actions'));
		});

		$("#export").click(exportAction);
	};
	return { init:init, getOwnerId:getOwnerId };
}());

$(document).ready(function() {
	more_actions.init();
});

