$(document).ready(function() {
	$('.more-actions').bind('change', function() {
		if ($(this).find('option:selected').val() == 'delete')
			deleteAction();
		else if($(this).find('option:selected').val() == 'rename')
			renameAction();
		else if($(this).find('option:selected').val() == 'export')
			exportAction();
		else if($(this).find('option:selected').val() == 'radioShow')
			radioShowAction();
	});
	
	$("#export").click(exportAction);
});

function renameAction() {
	var messageSection = $("#messageSection").val();
	$.ajax({
		type:'GET',
		url: url_root + messageSection + '/rename',
		data: {ownerId: $("#ownerId").val()},
		success: function(data) {
			launchSmallPopup('Rename ' + messageSection, data, 'Rename');
	}})
}

function deleteAction() {
	var messageSection = $("#messageSection").val();
	$.ajax({
		type:'GET',
		url: url_root + messageSection + '/confirmDelete',
		data: {id: $("#ownerId").val()},
		success: function(data) {
			launchSmallPopup('Delete ' + messageSection, data, 'Delete');
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
		data: {messageSection: $("#messageSection").val(), ownerId: $('#ownerId').val(), activityId: $("#activityId").val(),
				searchString: $("#searchString").val(), groupId: $("#groupId").val(), messageTotal: $("#messageTotal").val(),
				failed: $("#failed").val(), starred: $("#starred").val(), viewingArchive: viewingArchive},
		success: function(data) {
			launchSmallPopup('Export', data, 'Export');
			updateExportInfo();
	}})
}
