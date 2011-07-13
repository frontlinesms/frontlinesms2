$(document).ready(function() {
	$("#reply-dropdown").change(quickMessageClickAction);
});

function launchWizard(title, html) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#modalBox").dialog(
		{
			modal: true,
			title: title,
			width: 600,
			close: function() { $(this).remove(); }
		}
	);
	$("#tabs").tabs();
}

function quickMessageClickAction() {
	var me = $(this).find('option:selected');
	var messageType = me.text();
	if (messageType == 'Reply') {
		var src = $("#message-src").val()
	} else if(messageType == 'Forward') {
		var text = $("#message-body").text()
	}
	$.ajax({
		type:'POST',
		data: {recipient: src, messageText: text},
		url: '/frontlinesms2/quickMessage/create',
		success: function(data, textStatus){ launchWizard(messageType, data); }
	});
	$("#reply-dropdown").val("na");
}