function messageResponseClick(messageType) {
	$("#dropdown_options").hide();
	var configureTabs= "";
	var me = $(this);
	if (messageType == 'Reply') {
		configureTabs = "tabs-1, tabs-3"
		var src = $("#message-src").val();
	} else if(messageType == 'Forward') {
		var text = $("#message-body").text();
	}
	var messageSection = $('input:hidden[name=messageSection]').val();
	
	$.ajax({
		type:'POST',
		data: {recipient: src, messageText: text, configureTabs: configureTabs},
		url: url_root + 'quickMessage/create',
		success: function(data, textStatus){ launchMediumWizard(messageType, data, 'Send');addTabValidations(); }
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
			close: function() { $(this).remove(); },
			open: function() {$("#tabs-1").TabContentWidget();$("#tabs-2").TabContentWidget();$("#tabs-3").TabContentWidget()}
		}
	);
	changeButtons();
	$(".ui-tabs-nav li a ").click(changeButtons);
	onLoad && onLoad();
}

function cancel() {
	$(this).dialog('close');
}

function prevButton() {
	var $tabs = $('#tabs').tabs();
	var index = $tabs.tabs('option', 'selected');
	$tabs.tabs('select', index - 1);
	$(changeButtons);
}

function nextButton() {
	var tabs = $('#tabs').tabs();
	var index = tabs.tabs('option', 'selected');
	if($("#tabs-" + (index + 1)).TabContentWidget("validate")) {
		tabs.tabs('select', index + 1);
		$(changeButtons);
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


function toggleDropdown() {
	$("#dropdown_options").toggle();
}
