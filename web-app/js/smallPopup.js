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
	if ($("#modalBox").contentWidget("onDone")) {
		$(this).find("form").submit(); 
		$(this).remove();
	}
}
