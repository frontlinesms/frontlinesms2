$(document).ready(function() {
	$("#dropdown_options").hide()
	$("#btn_forward").click(quickMessageClickAction)
	$("#btn_reply" )
		.button()
		.click(quickMessageClickAction)
		.next()
			.button( {
				text: false,
				icons: {
					primary: "ui-icon-triangle-1-s"
				}
			})
			.click(function() {
				$("#dropdown_options").toggle()
			})
			.parent()
				.buttonset();
});

function launchWizard(title, html,width, onLoad) {
	var popupWidth = width ? width : 600
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#modalBox").dialog(
		{
			modal: true,
			title: title,
			width: popupWidth,
			close: function() { $(this).remove(); }
		}
	);
	$("#tabs").tabs();
	onLoad && onLoad();
}

function quickMessageClickAction() {
	var configureTabs
	$("#dropdown_options").hide()
	var me = $(this)
	var messageType = me.text();
	if (messageType == 'Reply') {
		configureTabs = 'tabs-1, tabs-3'
		var src = $("#message-src").val()
	} else if(messageType == 'Forward') {
		var text = $("#message-body").text()
	}
	$.ajax({
		type:'POST',
		traditional: true,
		context:'json',
		data: { recipients: [src],  messageText: text, configureTabs: configureTabs},
		url: '/frontlinesms2/quickMessage/create',
		success: function(data, textStatus){ launchWizard(messageType, data); }
	});
	$("#reply-dropdown").val("na");
}
