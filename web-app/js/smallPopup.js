function launchSmallPopup(title, html, btnFinishedText) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#modalBox").dialog(
		{
			modal: true,
			width: 285,
			title: title,
			buttons: [{ text:"Cancel", click: cancel, id:"cancel" },
			          		{ text:btnFinishedText,  click: doneAction, id:"done" }],
			close: function() { $(this).remove(); }
		}
	);
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

function cancel() {
	$(this).remove();
}

function doneAction() {
	if ($("#modalBox").contentWidget("onDone")) {
		$(this).find("form").submit(); 
		$(this).remove();
	}
}
