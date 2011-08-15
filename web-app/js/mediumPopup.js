$(document).ready(function() {
	$("#dropdown_options").hide()
	$("#btn_forward").click(quickMessageClickAction)
	$("#btn_reply" )
		.button()
		.click(quickMessageClickAction)
		.next()
			.button( {
				text: false,
			})
			.click(function() {
				$("#dropdown_options").toggle()
			})
			.parent()
				.buttonset();
});

function quickMessageClickAction() {
	$("#dropdown_options").hide();
	var me = $(this);
	var messageType = me.text();
	if (messageType == 'Reply') {
		var src = $("#message-src").val();
	} else if(messageType == 'Forward') {
		var text = $("#message-body").text();
	}
	var messageSection = $('input:hidden[name=messageSection]').val();
	
	$.ajax({
		type:'POST',
		data: {recipient: src, messageText: text},
		url: url_root + 'quickMessage/create',
		success: function(data, textStatus){ launchMediumWizard(messageType, data); }
	});
	$("#reply-dropdown").val("na");
}

function launchMediumWizard(title, html, btnFinishedText, onLoad) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#modalBox").dialog(
		{
			modal: true,
			title: title,
			width: 675,
			height: 500,
			buttons: [{ text:"Cancel", click: cancel, id:"cancel" },
			          		{ text:"Prev", click: prevButton, id:"prevPage" },
			          		{ text:"Next",  click: nextButton, id:"nextPage" },
			          		{ text:btnFinishedText,  click: done, id:"done" }],
			close: function() { $(this).remove(); }
		}
	);
	changeButtons();
	$(".ui-tabs-nav li a ").click(changeButtons);
	onLoad && onLoad();
}

function cancel() {
	$(this).remove();
}

function prevButton() {
	var $tabs = $('#tabs').tabs();
	var index = $tabs.tabs('option', 'selected');
	$tabs.tabs('select', index - 1);
	$(changeButtons);
}

function nextButton() {
	var $tabs = $('#tabs').tabs();
	var index = $tabs.tabs('option', 'selected');
	var validateInput = validate();
	if(validateInput) {
		$tabs.tabs('select', index + 1);
		$(changeButtons);
	} else {
		//display error
		displayError();
	}
}

function done() {
	$(this).find("form").submit(); // TODO add validation. Sould be able to add validate() function to individual popup gsp's so that this function works universally
	$(this).remove();
}

function changeButtons() {
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
