function chooseActivity() {
	var activity = $("#new-activity-choices input[checked=checked]").val();
	var activityUrl = activity + '/create';
	var title = i18n("wizard.title.new") + activity;
	$(this).dialog('close');
	$.ajax({
		type:'GET',
		dataType: "html",
		url: url_root + activityUrl,
		beforeSend: function(){ showThinking(); },
		success: function(data, textStatus) { hideThinking(); launchMediumWizard(title, data, i18n('wizard.create'), 675, 500); }
	});
	return;
}
	
function checkForSuccessfulSave(response, type) {
	$("#submit").removeAttr('disabled');
	if (response.ok) {
		$("#tabs").load(response.page + ".gsp", function() {
			$(".summary #ownerId").val(response.ownerId);
		});
		var messageDialog = $("#modalBox")
		messageDialog.dialog(
			{
				modal: true,
				title: i18n("popup.title.saved", type),
				buttons: [{ text:i18n("popup.cancel"), click: cancel, id:"cancel" }, { text:i18n("popup.back"), disabled: "true"},
				          		{ text:i18n('popup.ok'),  click: summaryRedirect, id:"submit" }],
				close: function() { $(this).remove(); }
			}
		);
		messageDialog.css("height", "389px");
		
	} else {
		var errors = $(".error-panel");
		errors.text(response.text);
		errors.show();
		$("#submit").removeAttr('disabled');
	}
}
	
function summaryRedirect(ownerId) {
	var ownerId = $(".summary #ownerId").val();
	$(this).dialog('close');
	window.location.replace(url_root + "message/activity/" + ownerId);
}