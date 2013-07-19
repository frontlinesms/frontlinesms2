$(function() {
	$('#group-actions').bind('change', function() {
		var selected = $(this).find('option:selected').val();
		if(selected) {
			groupActions[selected].call();
		}
	});
});

var groupActions = {
	"rename": function() {
		$.ajax({
			type:'GET',
			url: url_root + getContactSection() +'/rename',
			beforeSend : function() { showThinking(); },
			data: {groupId: $("#groupId").val(), groupName: $("#group-title").text().substring(0, $("#group-title").text().length-4)},
			success: function(data) {
				hideThinking();
				launchSmallPopup(i18n("smallpopup.group.rename.title"), data, i18n("action.rename"), 'validate'); }
		});
	},

	"edit": function() {
		$.ajax({
			type:'GET',
			url: url_root + getContactSection() + '/edit',
			beforeSend : function() { showThinking(); },
			data: {id: $("#groupId").val()},
			success: function(data) {
				hideThinking();
				mediumPopup.launchMediumPopup(i18n("smallpopup.group.edit.title"), data, i18n("action.edit"), mediumPopup.submit); }
		});
	},
	
	"delete": function() {
		$.ajax({
			type:'GET',
			url: url_root + getContactSection() + '/confirmDelete',
			beforeSend : function() { showThinking(); },
			data: {groupId: $("#groupId").val()},
			success: function(data){
				hideThinking();
				launchSmallPopup(i18n("smallpopup.group.delete.title"), data, i18n("action.ok")); }
		});
	}
};

function getContactSection() {
	var contactSection = $("#contactsSection").val();
	return contactSection;
}
