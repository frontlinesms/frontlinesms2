function chooseActivity() {
	var activity, activityUrl, title;
	activity = $("input[name='activity']:checked").val();
	activityUrl = activity.replace(/\s/g, "") + '/create';
	title = i18n("wizard.title.new") + activity;
	$(this).dialog('close');
	$.ajax({
		type:'GET',
		dataType: "html",
		url: url_root + activityUrl,
		beforeSend: function(){ showThinking(); },
		success: function(data, textStatus) { hideThinking(); mediumPopup.launchMediumWizard(title, data, i18n('wizard.create'), 675, 500, false); }
	});
}
	
function checkForSuccessfulSave(response, type) {
	$("#submit").removeAttr('disabled');
	if (response.ok) {
		loadSummaryTab(type);
	} else {
		displayErrors(response);
	}
}
	
function summaryRedirect() {
	var activityId = $(".summary #activityId").val();
	$(this).dialog('close');
	window.location.replace(url_root + "message/activity/" + activityId);
}

function loadSummaryTab(type) {
	var messageDialog;
	$("div.confirm").parent().hide();
		$(".ui-tabs-nav").hide();
		$("div.summary").show();
		$(".summary #activityId").val(response.ownerId);
		messageDialog = $("#modalBox");
		messageDialog.dialog(
			{
				modal: true,
				title: i18n("popup.title.saved", type),
				buttons: [
					{ text:i18n("action.cancel"), click: cancel, id:"cancel" },
					{ text:i18n("action.back"), disabled: "true"},
					{ text:i18n('action.ok'),  click: summaryRedirect, id:"submit" }],
				close: function() { $(this).remove(); }
			}
		);
		messageDialog.css("height", "389px");
}

function displayErrors(response) {
	var errors;
	errors = $(".error-panel");
	errors.text(response.text);
	errors.show();
	$("#submit").removeAttr('disabled');
}