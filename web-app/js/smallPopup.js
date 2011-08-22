function launchSmallPopup(title, html, btnFinishedText) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#modalBox").dialog(
		{
			modal: true,
			width: 285,
			title: title,
			buttons: [{ text:"Cancel", click: cancel, id:"cancel" },
			          		{ text:btnFinishedText,  click: done, id:"done" }],
			close: function() { $(this).remove(); }
		}
	);
}

function cancel() {
	$(this).remove();
}

function done() {
	$(this).find("form").submit(); // TODO add validation. Should be able to add validate() function to individual popup gsp's so that this function works universally
	$(this).remove();
}
