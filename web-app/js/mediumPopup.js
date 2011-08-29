function messageResponseClick(messageType) {
	$("#dropdown_options").hide();
	var configureTabs= "";
	var me = $(this);
	if (messageType == 'Reply') {
		configureTabs = "tabs-1, tabs-3, tabs-4"
		var src = $("#message-src").val();
	} else if(messageType == 'Forward') {
		var text = $("#message-body").text();
	}
	var messageSection = $('input:hidden[name=messageSection]').val();
	
	$.ajax({
		type:'POST',
		data: {recipients: src, messageText: text, configureTabs: configureTabs},
		url: url_root + 'quickMessage/create',
		success: function(data, textStatus){ launchMediumWizard(messageType, data, "Send", null, true);addTabValidations(); }
	});
	$("#reply-dropdown").val("na");
}



function launchMediumPopup(title, html, btnFinishedText) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#modalBox").dialog(
		{
			modal: true,
			width: 675,
			height: 500,
			title: title,
			buttons: [{ text:"Cancel", click: cancel, id:"cancel" },
			          		{ text:btnFinishedText,  click: function() {$("#tabs-1").contentWidget("onDone")}, id:"done" }],
			close: function() { $(this).remove(); }
		}
	);
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
			{ text:"Back", click: prevButton, id:"prevPage" },
			{ text:"Next",  click: nextButton, id:"nextPage" },
			{ text:"Done",  click: cancel, id:"confirmation" },
			{ text:btnFinishedText,  click: done, id:"done" }
		],
		close: function() {             
			$(this).remove();
		}
	});
	onTabSelect(withConfirmationScreen);
	changeButtons(getButtonToTabIndexMapping(withConfirmationScreen),  getCurrentTab())
	initializeTabContentWidgets()
	onLoad && onLoad();

}

function cancel() {
	$(this).dialog('close');
}

function prevButton() {
		for (var i = 1; i <= getCurrentTab(); i++) {
			var prevTabToSelect = getCurrentTab() - i;
			if ($.inArray(prevTabToSelect, $("#tabs").tabs("option", "disabled")) == -1) {
				$("#tabs").tabs('select', prevTabToSelect);
				break;
			}
		}
}

function nextButton() {
	if(validateCurrentTab()) {
		for (var i = 1; i <= getTabLength(); i++) {
			var nextTabToSelect = getCurrentTab() + i;
			if ($.inArray(nextTabToSelect, $("#tabs").tabs("option", "disabled")) == -1) {
				$("#tabs").tabs('select', nextTabToSelect);
				break;
			}
		}
	}
}

function done() {
	if(validateWholeTab() && onDoneOfCurrentTab()) {
		$(this).find("form").submit();                  
		$(this).remove();
	}
}

function validateWholeTab() {
	var isValid = true
	$.each($("#tabs").find('.ui-tabs-panel'), function(index, value) {
		isValid = isValid && $("#" + value.id).contentWidget('validate')

	});
  	return isValid
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

function validateCurrentTab() {
	var selected = $("#tabs").tabs( "option", "selected" );
	var currentTab = $("#tabs").find('.ui-tabs-panel').eq(selected)
	return currentTab.contentWidget("validate")
}

function onDoneOfCurrentTab() {
	var selected = $("#tabs").tabs( "option", "selected" );
	var currentTab = $("#tabs").find('.ui-tabs-panel').eq(selected)
	return currentTab.contentWidget("onDone")
}

function movingForward(nextIndex, currentIndex) {
	return nextIndex > currentIndex
}

function onTabSelect(withConfirmationScreen) {
	$('#tabs').tabs({select: function(event, ui) {
		var isValid = movingForward(ui.index, getCurrentTab()) ? validateCurrentTab() : true
		if (isValid) {
			changeButtons(getButtonToTabIndexMapping(withConfirmationScreen), ui.index)
		}
		return isValid
	}});
}

function initializeTabContentWidgets() {
	for(i=0; i <= getTabLength(); i++) {
		$("#tabs-" + (i + 1)).contentWidget()
	}
}

