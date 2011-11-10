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
		success: function(data, textStatus){ launchMediumWizard(messageType, data, "Send", true); }
	});
	$("#reply-dropdown").val("na");
}

function launchMediumPopup(title, html, btnFinishedText, submitAction) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#modalBox").dialog(
		{
			modal: true,
			width: 675,
			height: 500,
			title: title,
			buttons: [{ text:"Cancel", click: cancel, id:"cancel" }, { text:"Back", disabled: "true"},
			          		{ text:btnFinishedText,  click: submitAction, id:"submit" }],
			close: function() { $(this).remove(); }
		}
	);
	initializePopup();
}

function createSmartGroup() {}

function chooseActivity() {
	var activity = $("#activity-list input[checked=checked]").val();
	var activityUrl = activity + '/create';
	var title = 'New ' + activity;
	
	$(this).dialog('close');
	$.ajax({
		type:'GET',
		dataType: "html",
		url: url_root + activityUrl,
		success: function(data, textStatus){ launchMediumWizard(title, data, "Create", true); }
	});
	return;
}
 
function launchMediumWizard(title, html, btnFinishedText, hasConfirmationScreen) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#messageText").keyup()
	$("#modalBox").dialog({
		modal: true,
		title: title,
		width: 675,
		height: 500,
		buttons: [
			{ text:"Cancel", click: cancel, id:"cancel" },
			{ text:"Back", id:"disabledBack", disabled: true },
			{ text:"Back", click: prevButton, id:"prevPage" },
			{ text:"Next",  click: nextButton, id:"nextPage" },
			{ text:btnFinishedText,  click: submit, id:"submit" },
			{ text:"Done",  click: finished, id:"finished" }
		],
		close: function() { $(this).remove(); }
	});
	makeTabsUnfocusable();
	$('[class^="tabs-"]').bind('click', onTabSelect(hasConfirmationScreen));
	onTabSelect(hasConfirmationScreen);
	changeButtons(getButtonToTabIndexMapping(hasConfirmationScreen),  getCurrentTabDom())
	initializeTabContentWidgets()
	initializePopup();
}

function submit() {
	$("#submit").attr('disabled', 'disabled');
	if(validateWholeForm()) {
		$(this).find("form").submit();
		if(getCurrentTabIndex() == getTabLength())
			$(this).dialog('close');
	} else {
		$("#submit").removeAttr('disabled');
		$('.error-panel').show();
	}
}

function finished() {
	var title = $("#ui-dialog-title-modalBox").text();
	$(this).dialog('close');
	if(title == "New announcement")
		window.location.replace(url_root + "message/pending");
	else if(title == "New poll")
		window.location.replace(url_root + "message/pending");
	else if(title == "Quick message")
		window.location.replace(url_root + "message/pending");
	else if(title == "Create smart group")
		window.location.replace(url_root + "contact");
	else
		window.location.replace(url_root + "message");
}

function cancel() {
	$(this).dialog('close');
}

function prevButton() {
	for (var i = 1; i <= getCurrentTabDom(); i++) {
		var prevTabToSelect = getCurrentTabDom() - i;
		if ($.inArray(prevTabToSelect, $("#tabs").tabs("option", "disabled")) == -1) {
			$("#tabs").tabs('select', prevTabToSelect);
				break;
		}
	}
}

function nextButton() {
	if(validateCurrentTab()) {
		for (var i = 1; i <= getTabLength(); i++) {
			var nextTabToSelect = getCurrentTabDom() + i;
			if ($.inArray(nextTabToSelect, $("#tabs").tabs("option", "disabled")) == -1) {
				$("#tabs").tabs('select', nextTabToSelect);
				break;
			}
		}
		$("#tabs-" + getCurrentTabDom()).find('input', 'textarea', 'textfield').first().focus();
	}
}

function goToSummaryTab() {
	$("#tabs").tabs("enable", getTabLength());
	moveToRelativeTab(1);
}

function validateWholeForm() {
	var isValid = true;
	$("#tabs").find('.ui-tabs-panel').each(function(index, value) {
		isValid = isValid && validateTab($("#" + value.id))
	});
  	return isValid;
}

function changeButtons(buttonToTabIndexMapping, tabIndex) {
	$.each(buttonToTabIndexMapping, function(key, value) {
		if (value.indexOf(tabIndex) != -1) {
			$(".ui-dialog-buttonpane #" + key).show()
		} else {
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

function makeTabsUnfocusable() {
	$("#tabs").find('input', 'textarea', 'textfield').first().focus();
	$('a[href^="#tabs"]').attr('tabindex', '-1');
}

function getTabLength() {
	return $('#tabs').tabs("length") - 1;
}

function getButtonToTabIndexMapping(hasConfirmationScreen) {
	return {
			"cancel" : range(0, hasConfirmationScreen ? getTabLength() - 1 : getTabLength()),
			"prevPage": range(1, hasConfirmationScreen ? getTabLength() - 1 : getTabLength()),
			"nextPage": range(0, hasConfirmationScreen ? getTabLength() - 2 : getTabLength() - 1),
			"submit": hasConfirmationScreen ? [getTabLength() - 1] : [getTabLength()],
			"finished": hasConfirmationScreen ? [getTabLength()] : [],
			"disabledBack": [0]
		}
}

function validateCurrentTab() {
	return validateTab(getCurrentTab())
}

function getCurrentTab() {
	var selected = $("#tabs").tabs( "option", "selected" );
	return $("#tabs").find('.ui-tabs-panel').eq(selected)
}

function getCurrentTabDom() {
	return $('#tabs').tabs('option', 'selected');
}

function getCurrentTabIndex() {
	var tabWidget = $('#tabs').tabs();
	var current = tabWidget.tabs('option', 'selected')
	return current;
}

function movingForward(nextIndex, currentIndex) {
	return nextIndex > currentIndex
}

function onTabSelect(hasConfirmationScreen) {
	$('#tabs').tabs({select: function(event, ui) {
		var isValid = movingForward(ui.index, getCurrentTabIndex()) ? validateCurrentTab() : true;
		if (isValid) {
			if($('.error-panel'))
				$('.error-panel').hide();
			changeButtons(getButtonToTabIndexMapping(hasConfirmationScreen), ui.index)
		}
		return isValid;
	}});
}

function initializeTabContentWidgets() {
	for(i=0; i <= getTabLength(); i++) {
		$("#tabs-" + (i + 1)).contentWidget()
	}
}

function validateTab(tab) {
	var isValid = tab.contentWidget('validate');
	if(!isValid) {
		$('.error-panel').show();
	}
	return isValid;
}

function disableTab(tabNumber) {
	$('#tabs').tabs("disable", tabNumber);
	$('.tabs-' + (tabNumber + 1)).addClass('disabled-tab');
}

function enableTab(tabNumber) {
	$('#tabs').tabs("enable", tabNumber);
	$('.tabs-' + (tabNumber + 1)).removeClass('disabled-tab');
}

function moveToRelativeTab(offset) {
	$('#tabs').tabs('select', getCurrentTabIndex() + offset);
}
