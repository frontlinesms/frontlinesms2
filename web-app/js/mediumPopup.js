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

function chooseType() {
	alert('chosen');
}

function quickMessageClickAction() {
	$("#dropdown_options").hide();
	var configureTabs= ""
	var me = $(this);
	var messageType = me.text();
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
		success: function(data, textStatus){ launchMediumWizard(messageType, data);addTabValidations(); }
	});
	$("#reply-dropdown").val("na");
}

var buttonIndex = {}

function launchMediumWizard(title, html, btnFinishedText, onLoad, withConfirmation) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#modalBox").dialog({
		modal: true,
		title: title,
		width: 675,
		height: 500,
		buttons: [
			{ text:"Cancel", click: cancel, id:"cancel" },
			{ text:"Prev", click: prevButton, id:"prevPage" },
			{ text:"Next",  click: nextButton, id:"nextPage" },
			{ text:"Confirm",  click: cancel, id:"confirmation" },
			{ text:btnFinishedText,  click: done, id:"done" }
		],
		close: function() {             
			$(this).remove();
		}
	});
	var $tabs = $('#tabs').tabs();
	var tabLength =$(".ui-tabs-panel").size() - 1;
	onLoad && onLoad();
	
//	$(".ui-tabs-nav li a ").click(changeButtons);
	buttonIndex = {
		"cancel" : range(0, withConfirmation ? tabLength - 1 : tabLength),
		"prevPage": range(1, withConfirmation ? tabLength - 1 : tabLength),
		"nextPage": range(0, withConfirmation ? tabLength - 2 : tabLength - 1),
		"done": withConfirmation ? [tabLength - 1] : [tabLength],
		"confirmation": withConfirmation ? [tabLength] : []
	}
	changeButtons(0);
//	console.log($(".ui-tabs-panel").size())

}

function cancel() {
	$(this).dialog('close');
}

function prevButton() {
	var $tabs = $('#tabs').tabs();
	var index = $tabs.tabs('option', 'selected');
	$tabs.tabs('select', index - 1);
	changeButtons(index - 1);
}

function nextButton() {
	var tabs = $('#tabs').tabs();
	var index = tabs.tabs('option', 'selected');
	if($("#tabs-" + (index + 1)).TabContentWidget("validate")) {
		tabs.tabs('select', index + 1);
		changeButtons(index + 1)
	}
}

function moveToNextTab() {
	var tabs = $('#tabs').tabs();
	var index = tabs.tabs('option', 'selected');
	tabs.tabs('select', index + 1);
	changeButtons(index + 1)
}

function done() {
	// TODO add validation. Sould be able to add validate() function to individual popup gsp's so that this function works universally
	var tabs = $('#tabs').tabs();
	var index = tabs.tabs('option', 'selected');
	if($("#tabs-" + (index + 1)).TabContentWidget("validate")) {
		$(this).find("form").submit();
		$(this).remove();
	}
}

function changeButtons(currentTabIndex) {
	$.each(buttonIndex, function(key, value) {
		if (value.indexOf(currentTabIndex) != -1)
		{
			$(".ui-dialog-buttonpane #" + key).show()
		}
	else
		{
			$(".ui-dialog-buttonpane #" + key).hide()
		}

	});
}

function range(first, last) {
	var a = []
	for (var i = first; i <= last; i++) {
		a.push(i)
	}
	return a
}


