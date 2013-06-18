function launchSmallPopup(title, html, btnFinishedText, doneAction) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	if (doneAction === undefined) {
		doneAction = defaultDoneAction;
	} else {
		if (doneAction === 'validate') {
			doneAction = smallPopup.validate;
		}
	}
	$("#modalBox").dialog({
			modal: true,
			width: 315,
			maxHeight: 300,
			title: title,
			buttons: [{ text:i18n("action.cancel"), click: cancel, id:"cancel" },
					{ text:btnFinishedText,  click: doneAction, id:"done" }],
			close: function() { $(this).remove(); }
	});
	$("#modalBox").bind("keydown", function(e) {
		if (e.keyCode === 13){
			$("#done").click();
			return false;
		}
	});
}

function launchConfirmationPopup(title) {
	var contactList, contactIdList, message, count;
	contactList = getCheckedList('contact');
	if (contactList === ',') {
		contactIdList = $("#contactId").val();
		message = i18n("smallpopup.delete.prompt", $('#name').val());
	} else {
		contactIdList = contactList;
		count = contactList.split(",").length - 2;
		message = i18n("smallpopup.delete.many.prompt", count);
	}
	$.ajax({
		type:'POST',
		data: { checkedContactList:contactIdList, message:message },
		url: url_root + 'contact/confirmDelete',
		beforeSend : function() { showThinking(); },
		success: function(data, textStatus) {
			hideThinking();
			launchSmallPopup(title, data, i18n('action.ok'));
		}
	});
	return false;
}

function launchEmptyTrashConfirmation() {
	$("#trash-actions").val("na");
	$.ajax({
		type:'POST',
		url: url_root + 'message/confirmEmptyTrash',
		success: function(data, textStatus){ launchSmallPopup(i18n("smallpopup.empty.trash.prompt"), data, i18n('action.ok')); }
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

var smallPopup = (function() {
	var
		_checkResults = function(json) {
			if (json.ok) {
				$("#modalBox").remove();
				location.reload(true);
			} else {
				$("#smallpopup-error-panel").html(json.text);
				$("#smallpopup-error-panel").show();
			}
		},

		_validate = function() {
			if ($("#modalBox").contentWidget("onDone")) {
				$(this).find("form").submit();
			}
		};
	return {
		checkResults: _checkResults,
		validate: _validate
	};
}());
