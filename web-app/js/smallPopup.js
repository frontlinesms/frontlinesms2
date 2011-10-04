function launchSmallPopup(title, html, btnFinishedText, doneAction) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	if (doneAction == null) { doneAction = defaultDoneAction }
	$("#modalBox").dialog({
			modal: true,
			width: 285,
			maxHeight: 300,
			title: title,
			buttons: [{ text:"Cancel", click: cancel, id:"cancel" },
			          		{ text:btnFinishedText,  click: doneAction, id:"done" }],
			close: function() { $(this).remove(); }
	});
}

function launchConfirmationPopup(title) {
	var contactList = $("#checkedContactList");
	if (contactList.val() == ',') {
		var contactIdList = $("#contactId").val();
		var message = "Delete " + $('#name').val() + "?";
	} else {
		var contactIdList = contactList.val();
		var count = contactList.val().split(",").length - 2;
		var message = "Delete " + count + " contacts?"
	}
	
	$.ajax({
		type:'POST',
		data: {checkedContactList: contactIdList, message: message},
		url: url_root + 'contact/confirmDelete',
		success: function(data, textStatus){ launchSmallPopup(title, data, "Ok"); }
	});
}

function launchEmptyTrashConfirmation() {
	$("#trash-actions").val("na");
	$.ajax({
		type:'POST',
		url: url_root + 'message/confirmEmptyTrash',
		success: function(data, textStatus){ launchSmallPopup('Empty Trash?', data, "Ok"); }
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
