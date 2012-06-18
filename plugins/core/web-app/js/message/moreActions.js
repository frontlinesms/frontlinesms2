$(document).ready(function() {
	$('.more-actions').bind('change', function() {
		if ($(this).find('option:selected').val() == 'delete')
			deleteAction();
		else if($(this).find('option:selected').val() == 'rename')
			renameAction();
		else if($(this).find('option:selected').val() == 'edit')
			editAction();
		else if($(this).find('option:selected').val() == 'export')
			exportAction();
		else if($(this).find('option:selected').val() == 'radioShow')
			radioShowAction();
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
			hideThinking(); launchSmallPopup(i18n("smallpopup.fmessage.rename.title", messageSection), data, i18n("action.rename"));
	}})
}

function editAction() {
	var messageSection = $("#messageSection").val();
	var title = i18n("wizard.fmessage.edit.title", messageSection);
	$.ajax({
		type:'GET',
		url: url_root + messageSection + '/edit',
		data: {id: $("#ownerId").val()},
		beforeSend: function(){ showThinking(); },
		success: function(data) {
			hideThinking(); launchMediumWizard(title, data, i18n('wizard.ok'), 675, 500, false);
	}})
}

function deleteAction() {
	var messageSection = $("#messageSection").val();
	$.ajax({
		type:'GET',
		url: url_root + messageSection + '/confirmDelete',
		data: {id: $("#ownerId").val()},
		success: function(data) {
			launchSmallPopup(i18n("smallpopup.fmessage.delete.title", messageSection), data, i18n("action.delete"));
	}})
}

function exportAction() {
	var viewingArchive;
	if(url.indexOf("/archive/") >= 0)
		viewingArchive = true;
	else
		viewingArchive = false;
		
	$.ajax({
		type:'GET',
		url: url_root + 'export/messageWizard',
		data: {messageSection: $("#messageSection").val(), ownerId: $('#ownerId').val(),
				searchString: $("#searchString").val(), groupId: $("#groupId").val(), messageTotal: $("#messageTotal").val(),
				failed: $("#failed").val(), starred: $("#starred").val(), viewingArchive: viewingArchive},
		success: function(data) {
			launchSmallPopup(i18n("smallpopup.fmessage.export.title"), data, i18n("action.export"));
			updateExportInfo();
	}})
}
