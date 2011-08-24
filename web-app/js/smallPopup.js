function launchSmallPopup(title, html, btnFinishedText, width) {
	if(!width) {width = 285}
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#modalBox").dialog(
		{
			modal: true,
			width: width,
			title: title,
			buttons: [{ text:"Cancel", click: cancel, id:"cancel" },
			          		{ text:btnFinishedText,  click: doneAction, id:"done" }],
			close: function() { $(this).remove(); }
		}
	);
}

function cancel() {
	$(this).remove();
}

function doneAction() {
	if ($("#modalBox").contentWidget("validate")) {
		$(this).find("form").submit(); // TODO add validation. Should be able to add validate() function to individual popup gsp's so that this function works universally
		$(this).remove();
	}
}
