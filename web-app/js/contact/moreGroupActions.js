$(document).ready(function() {
	$('#group-actions').bind('change', function() {
		if($(this).find('option:selected').val() == 'rename')
			renameGroup();
		else if ($(this).find('option:selected').val() == 'delete')
			deleteGroup();
	});
});

function renameGroup() {
	$.ajax({
		type:'GET',
		url: url_root + 'group/rename',
		data: {groupId: $("#groupId").val()},
		success: function(data){
			launchSmallPopup('Rename group', data, 'Rename');
	}})
}

function deleteGroup() {
	$.ajax({
		type:'GET',
		url: url_root + 'group/confirmDelete',
		data: {groupId: $("#groupId").val()},
		success: function(data){
			launchSmallPopup('Delete group', data, 'Ok');
	}})
}