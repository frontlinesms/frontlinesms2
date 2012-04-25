function chooseActivity() {
	var activity = $("#new-activity-choices input[checked=checked]").val();
	var activityUrl = activity + '/create';
	var title = i18n("wizard.title.new") + activity;
	$(this).dialog('close');
	$.ajax({
		type:'GET',
		dataType: "html",
		url: url_root + activityUrl,
		success: function(data, textStatus) { launchMediumWizard(title, data, "Create", 675, 500); }
	});
	return;
}
	
function checkForSuccessfulSave(html, type) {
	if ($(html).find("#ownerId").val())
		launchMediumPopup(i18n("popup.title.saved", type), html, i18n('popup.ok'), summaryRedirect);
	else
		addFailedFlashMessage(html);
}
	
function summaryRedirect() {
	var ownerId = $(".summary #ownerId").val();
	$(this).dialog('close');
	window.location.replace(url_root + "message/activity/" + ownerId);
}

function addFailedFlashMessage(data) {
	alert(data);
	$("#notifications").prepend("<div class='flash message'>" + data + "<a class='hide-flash'>x</a></div>");
}
