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

function quickMessageClickAction() {
	$("#dropdown_options").hide()
	var me = $(this)
	var messageType = me.text();
	if (messageType == 'Reply') {
		var src = $("#message-src").val()
	} else if(messageType == 'Forward') {
		var text = $("#message-body").text()
	}
	var messageSection = $('input:hidden[name=messageSection]').val();
	
	$.ajax({
		type:'POST',
		data: {recipient: src, messageText: text},
		url: url_root + 'quickMessage/create',
		success: function(data, textStatus){ launchWizard(messageType, data); }
	});
	$("#reply-dropdown").val("na");
}

function launchWizard(title, html) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#modalBox").dialog(
		{
			modal: true,
			title: title,
			width: 675,
			height: 500,
			buttons: [{ text:"Prev", click: prevButton, id:"prevPage" },
			          		{ text:"Next",  click: nextButton, id:"nextPage" },
			          		{ text:"Done",  click: done, id:"done" }],
			close: function() { $(this).remove(); }
		}
	);
	popupButtons();
	$(".ui-tabs-nav li a ").click(popupButtons);
}

function prevButton() {
	var $tabs = $('#tabs').tabs();
	var index = $tabs.tabs('option', 'selected');
	$tabs.tabs('select', index - 1);
	$(popupButtons);
}

function nextButton() {
	var $tabs = $('#tabs').tabs();
	var index = $tabs.tabs('option', 'selected');
	$tabs.tabs('select', index + 1);
	$(popupButtons);
}

function done() {
	$(this).remove();
}

function popupButtons() {
	var $tabs = $('#tabs').tabs();
	var index = $tabs.tabs('option', 'selected');
	var totalSize = $(".ui-tabs-panel").size() - 1;
	if (index != totalSize) {
		$(".ui-dialog-buttonpane #prevPage").show();
		$(".ui-dialog-buttonpane #nextPage").show();
		$(".ui-dialog-buttonpane #done").hide();
	}
	if (index != 0) { 
		$(".ui-dialog-buttonpane #prevPage").show();
		$(".ui-dialog-buttonpane #nextPage").show();
	}
	if (index == 0) { 
		$(".ui-dialog-buttonpane #prevPage").hide();
		$(".ui-dialog-buttonpane #nextPage").show();
	}
	if (index == totalSize) {
		$(".ui-dialog-buttonpane #nextPage").hide();
		$(".ui-dialog-buttonpane #done").show();
	}
}
