function launchSmallPopup(title, html, btnFinishedText, doneAction) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	if (doneAction == null) { doneAction = defaultDoneAction }
	$("#modalBox").dialog({
			modal: true,
			width: 315,
			maxHeight: 300,
			title: title,
			buttons: [{ text:i18n("smallpopup.cancel"), click: cancel, id:"cancel" },
			          		{ text:btnFinishedText,  click: doneAction, id:"done" }],
			close: function() { $(this).remove(); }
	});
}

function launchConfirmationPopup(title) {
	var contactList = $("#checkedContactList");
	if (contactList.val() == ',') {
		var contactIdList = $("#contactId").val();
		var message = i18n("smallpopup.delete.prompt", $('#name').val());
	} else {
		var contactIdList = contactList.val();
		var count = contactList.val().split(",").length - 2;
		var message = i18n("smallpopup.delete.many.prompt", count)
	}
	
	$.ajax({
		type:'POST',
		data: {checkedContactList: contactIdList, message: message},
		url: url_root + 'contact/confirmDelete',
		success: function(data, textStatus){ launchSmallPopup(title, data, i18n('smallpopup.ok')); }
	});
}

function launchEmptyTrashConfirmation() {
	$("#trash-actions").val("na");
	$.ajax({
		type:'POST',
		url: url_root + 'message/confirmEmptyTrash',
		success: function(data, textStatus){ launchSmallPopup(i18n("smallpopup.empty.trash.prompt"), data, i18n('smallpopup.ok')); }
	});
}

function cancel() {
	$(this).remove();
}

function defaultDoneAction() {
	if ($("#modalBox").contentWidget("onDone")) {
		$(this).find("form").submit(); 
		$(this).remove();
	}
}
