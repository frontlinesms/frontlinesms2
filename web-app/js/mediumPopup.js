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
		success: function(data, textStatus){ launchMediumWizard(messageType, data, "Send", null, true); addTabValidations(); }
	});
	$("#reply-dropdown").val("na");
}

function launchMediumPopup(title, html, btnFinishedText) {
	launchMediumPopup(title, html, btnFinishedText, null);
}

function launchGenericMediumPopup(title, html, btnFinishedText, onLoad) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#modalBox").dialog(
		{
			modal: true,
			width: 675,
			height: 500,
			title: title,
			buttons: [{ text:"Cancel", click: cancel, id:"cancel" }, { text:"Back", disabled: "true"},
			          		{ text:btnFinishedText,  click: mediumPopupDone, id:"done" }],
			close: function() { $(this).remove(); }
		}
	);
	if(onLoad!=null) onLoad();
}

function launchActivityMediumPopup(title, html, btnFinishedText) {
	$("<div id='modalBox'><div>").html(html).appendTo(document.body);
	$("#modalBox").dialog(
		{
			modal: true,
			width: 675,
			height: 500,
			title: title,
			buttons: [{ text:"Cancel", click: cancel, id:"cancel" }, { text:"Back", disabled: "true"},
			          		{ text:btnFinishedText,  click:chooseActivity, id:"choose" }],
			close: function() { $(this).remove(); }
		}
	);
}

function chooseActivity() {
	var activity = $("#activity-list input[checked=checked]").val();
	var activityUrl = activity + '/create';
	var title = 'New ' + activity;
	
	$(this).dialog('close');
	$.ajax({
		type:'GET',
		dataType: "html",
		url: url_root + activityUrl,
		success: function(data, textStatus){ launchMediumWizard(title, data, "Create", function() { initialize(); }, true); }
	});
	return;
}

function mediumPopupDone() {
	var isValid = $("#tabs-1").contentWidget('validate');
	
	if(isValid) {
		$("#tabs-1").contentWidget("onDone");
		$(this).remove();
	} else {
		// show the error panel
		$('.error-panel').show();
	}
}
 
function launchMediumWizard(title, html, btnFinishedText, onLoad, withConfirmationScreen) {
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
			{ text:"Done",  click: finished, id:"confirmation" },
			{ text:btnFinishedText,  click: done, id:"done" }
		],
		close: function() {             
			$(this).remove();
		}
	});
	makeTabsUnfocusable();
	onTabSelect(withConfirmationScreen);
	changeButtons(getButtonToTabIndexMapping(withConfirmationScreen),  getCurrentTab())
	initializeTabContentWidgets()
	if(onLoad!=null) onLoad();
}

function cancel() {
	$(this).dialog('close');
}

function finished() {
	$(this).dialog('close');
	window.location.replace(url_root + "message/pending");
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
	$("#tabs-" + getCurrentTab()).find('input', 'textarea', 'textfield').first().focus();
	for (var i = 1; i <= getTabLength(); i++) {
		var nextTabToSelect = getCurrentTab() + i;
		if ($.inArray(nextTabToSelect, $("#tabs").tabs("option", "disabled")) == -1) {
			$("#tabs").tabs('select', nextTabToSelect);
			break;
		}
	}
}

function done() {
	$("#done").attr('disabled', 'disabled');
	if(validateWholeTab() && onDoneOfCurrentTab()) {
		$(this).find("form").submit();                  
		$(this).remove();
	} else {
		$("#done").removeAttr('disabled');
	}
}

function validateWholeTab() {
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

function getCurrentTab() {
	return $('#tabs').tabs('option', 'selected');
}

function getButtonToTabIndexMapping(withConfirmationScreen) {
	return {
			"cancel" : range(0, withConfirmationScreen ? getTabLength() - 1 : getTabLength()),
			"prevPage": range(1, withConfirmationScreen ? getTabLength() - 1 : getTabLength()),
			"nextPage": range(0, withConfirmationScreen ? getTabLength() - 2 : getTabLength() - 1),
			"done": withConfirmationScreen ? [getTabLength() - 1] : [getTabLength()],
			"confirmation": withConfirmationScreen ? [getTabLength()] : [],
			"disabledBack": [0]
		}
}

function validateCurrentTab() {
	return validateTab(getCurrentTabWidget())
}

function onDoneOfCurrentTab() {
	return getCurrentTabWidget().contentWidget("onDone")
}

function getCurrentTabWidget() {
	var selected = $("#tabs").tabs( "option", "selected" );
	return $("#tabs").find('.ui-tabs-panel').eq(selected)
}

function movingForward(nextIndex, currentIndex) {
	return nextIndex > currentIndex
}

function onTabSelect(withConfirmationScreen) {
	$('#tabs').tabs({select: function(event, ui) {
		var isValid = movingForward(ui.index, getCurrentTab()) ? validateCurrentTab() : true;
		if (isValid) {
			if($('.error-panel')) $('.error-panel').hide();
			changeButtons(getButtonToTabIndexMapping(withConfirmationScreen), ui.index)
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
function moveToTabBy(index) {
	var tabWidget = $('#tabs').tabs();
	var selected = tabWidget.tabs('option', 'selected')
	tabWidget.tabs('select', selected + index);
	return false;
}

function moveToNextTab(canMoveToNextTab, onFailure, onSuccess) {
	onSuccess = onSuccess || null
	if (canMoveToNextTab) {
		if (onSuccess != null)
			onSuccess()
		else
			moveToTabBy(1);
	}
	else
		onFailure()
	return false

}

$('.next').live('click', function() {
	if($(this).hasClass('disabled')) return;
	return moveToNextTab(true);
});

$('.back').live('click', function() {
	return moveToTabBy(-1);
});

$.widget("ui.contentWidget", {
	validate: function() {
		return this.options['validate'].call();			
	},

	onDone: function() {
		return this.options['onDone'].call();			
	},

	options: {validate: function() {return true;} ,
	onDone: function() {return true;}}
});
