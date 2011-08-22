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
		success: function(data, textStatus){ launchMediumWizard(messageType, data, "Send", null, true);addTabValidations(); }
	});
	$("#reply-dropdown").val("na");
}

function launchMediumWizard(title, html, btnFinishedText, onLoad, withConfirmationScreen) {

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
			{ text:"Done",  click: cancel, id:"confirmation" },
			{ text:btnFinishedText,  click: done, id:"done" }
		],
		close: function() {             
			$(this).remove();
		}
	});
	$('#tabs').tabs({select: function(event, ui) {
		changeButtons(getButtonToTabIndexMapping(withConfirmationScreen), ui.index)
	}});
	changeButtons(getButtonToTabIndexMapping(withConfirmationScreen),  getCurrentTab())
	onLoad && onLoad();

}

function cancel() {
	$(this).dialog('close');
}

function prevButton() {
	$("#tabs").tabs('select', getCurrentTab() - 1);
}

function nextButton() {
	var tabIndex = getCurrentTab() + 1;
	if($("#tabs-" + (tabIndex)).TabContentWidget("validate")) {
		$("#tabs").tabs('select', getCurrentTab() + 1);
	}
}

function done() {
	// TODO add validation. Sould be able to add validate() function to individual popup gsp's so that this function works universally
	var tabIndex = getCurrentTab() + 1;
	if($("#tabs-" + (tabIndex)).TabContentWidget("validate")) {
		$(this).find("form").submit();
		$(this).remove();
	}
}

function changeButtons(buttonToTabIndexMapping, tabIndex) {
	$.each(buttonToTabIndexMapping, function(key, value) {
		if (value.indexOf(tabIndex) != -1)
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

function getTabLength() {
	return $('#tabs').tabs("length") - 1;
}

function getCurrentTab() {
	return $('#tabs').tabs('option', 'selected');
}

function getButtonToTabIndexMapping(withConfirmationScreen) {
	return {
			"cancel" : range(0, withConfirmationScreen ? getTabLength() - 1 : getTabLength()),
			"prevPage": range(1, withConfirmationScreen ? getTabLength() - 1 : getTabLength()),
			"nextPage": range(0, withConfirmationScreen ? getTabLength() - 2 : getTabLength() - 1),
			"done": withConfirmationScreen ? [getTabLength() - 1] : [getTabLength()],
			"confirmation": withConfirmationScreen ? [getTabLength()] : []
		}
}


