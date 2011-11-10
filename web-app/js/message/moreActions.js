$(document).ready(function() {
	$('.more-actions').bind('change', function() {
		if ($(this).find('option:selected').val() == 'delete')
			deleteAction();
		else if($(this).find('option:selected').val() == 'rename')
			renameAction();
		else if($(this).find('option:selected').val() == 'export')
			exportAction();
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
	$.ajax({
		type:'GET',
		url: url_root + 'export/wizard',
		data: {messageSection: $("#messageSection").val(), ownerId: $('#ownerId').val(), activityId: $("#activityId").val(),
				searchString: $("#searchString").val(), groupId: $("#groupId").val(), messageTotal: $("#messageTotal").val(),
				failed: $("#failed").val(), starred: $("#starred").val(), viewingArchive: $("#viewingArchive").val()},
		success: function(data) {
			launchSmallPopup('Export', data, 'Export');
			updateExportInfo();
	}})
}