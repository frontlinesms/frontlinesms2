$(function() {
	$('#group-actions').bind('change', function() {
		var selected = $(this).find('option:selected').val();
		if(selected)
			groupActions[selected].call();
	});
});

var groupActions = {
	"rename": function() {
		$.ajax({
			type:'GET',
			url: url_root + getContactSection() +'/rename',
			data: {groupId: $("#groupId").val()},
			success: function(data){
				launchSmallPopup('Rename group', data, 'Rename');
		}})
	},
	
	"delete": function() {
		$.ajax({
			type:'GET',
			url: url_root + getContactSection() + '/confirmDelete',
			data: {groupId: $("#groupId").val()},
			success: function(data){
				launchSmallPopup('Delete group', data, 'Ok');
		}})
	}
}

function getContactSection() {
	var contactSection = $("#contactsSection").val()
	return contactSection
}