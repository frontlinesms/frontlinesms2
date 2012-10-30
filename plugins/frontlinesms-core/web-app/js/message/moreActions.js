$(document).ready(function() {
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
});

function renameAction() {
	var messageSection = $("#messageSection").val();
	$.ajax({
		type:'GET',
		url: url_root + messageSection + '/rename',
		data: {ownerId: $("#ownerId").val()},
		beforeSend: function(){ showThinking(); },
		success: function(data) {
			hideThinking(); launchSmallPopup(i18n("smallpopup.fmessage.rename.title", messageSection), data, i18n("action.rename"), 'validate'); }
	});
}

function editAction() {
	var title, messageSection;
	messageSection = $("#messageSection").val();
	title = i18n("wizard.fmessage.edit.title", messageSection);
	$.ajax({
		type:'GET',
		url: url_root + messageSection + '/edit',
		data: {id: $("#ownerId").val()},
		beforeSend: function(){ showThinking(); },
		success: function(data) {
			hideThinking(); mediumPopup.launchMediumWizard(title, data, i18n('wizard.ok'), 675, 500, false); }
	});
}

function deleteAction() {
	var messageSection = $("#messageSection").val();
	$.ajax({
		type:'GET',
		url: url_root + messageSection + '/confirmDelete',
		data: {id: $("#ownerId").val()},
		success: function(data) {
			launchSmallPopup(i18n("smallpopup.fmessage.delete.title", messageSection), data, i18n("action.delete")); }
	});
}

function exportAction() {
	var viewingArchive, params;
	viewingArchive = url.indexOf("/archive/") !== -1;
	params = {
			messageSection: $("#messageSection").val(),
			ownerId: $('input:hidden[name=ownerId]').val(),
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
		beforeSend: function(){ showThinking(); },
		success: function(data) {
			hideThinking(); launchSmallPopup(i18n("smallpopup.fmessage.export.title"), data, i18n("action.export"));
			updateExportInfo(); }
	});
}

