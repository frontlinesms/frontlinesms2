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
			data: {groupId: $("#groupId").val(), groupName: $("#group-title").text().substring(0, $("#group-title").text().length-4)},
			success: function(data){
				launchSmallPopup(i18n("smallpopup.group.rename.title"), data, i18n("smallpopup.rename"));
		}})
	},

	"edit": function() {
		$.ajax({
			type:'GET',
			url: url_root + getContactSection() + '/edit',
			data: {id: $("#groupId").val()},
			success: function(data) {
				launchMediumPopup(i18n("smallpopup.group.edit.title"), data, i18n('popup.edit'), submit);
		}})
	},
	
	"delete": function() {
		$.ajax({
			type:'GET',
			url: url_root + getContactSection() + '/confirmDelete',
			data: {groupId: $("#groupId").val()},
			success: function(data){
				launchSmallPopup(i18n("smallpopup.group.delete.title"), data, i18n('smallpopup.ok'));
		}})
	}
}

function getContactSection() {
	var contactSection = $("#contactsSection").val()
	return contactSection
}
