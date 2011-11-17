function messageResponseClick(messageType) {
	$("#dropdown_options").hide();
	var configureTabs= "";
	var me = $(this);
	if (messageType == 'Reply') {
		configureTabs = "tabs-1, tabs-3, tabs-4"
		var src = $("#message-src").val();
	} else if(messageType == 'Forward') {
		var text = $("#single-message #message-detail-content p").text();
	}
	var messageSection = $('input:hidden[name=messageSection]').val();
	
	$.ajax({
		type:'POST',
		data: {recipients: src, messageText: text, configureTabs: configureTabs},
		url: url_root + 'quickMessage/create',
		success: function(data, textStatus){ launchMediumWizard(messageType, data, "Send"); }
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
 
function launchMediumWizard(title, html, btnFinishedText) {
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
			{ text:btnFinishedText,  click: submit, id:"submit" }
		],
		close: function() { $(this).remove(); }
	});
	makeTabsUnfocusable();
	validateTabSelections();
	changeButtons(getButtonToTabMappings(),  getCurrentTabDom());
	initializeTabContentWidgets();
	initializePopup();
}

function submit() {
	$("#submit").attr('disabled', 'disabled');
	if(tabValidates(getCurrentTab())) {
		$(this).find("form").submit();
		$(this).dialog('close');
	} else {
		$("#submit").removeAttr('disabled');
		$('.error-panel').show();
	}
}

function prevButton() {
	for (var i = 1; i <= getCurrentTabIndex(); i++) {
		var prevTab = getCurrentTabIndex() - i;
		if ($.inArray(prevTab, $("#tabs").tabs("option", "disabled")) == -1) {
			$("#tabs").tabs('select', prevTab);
			break;
		}
	}
}

function nextButton() {
	for (var i = 1; i <= getTabLength(); i++) {
		var nextTab = getCurrentTabIndex() + i;
		if ($.inArray(nextTab, $("#tabs").tabs("option", "disabled")) == -1) {
			$("#tabs").tabs('select', nextTab);
			break;
		}
	}
}

function cancel() {
	$(this).dialog('close');
}

function validateTabSelections() {
	$('#tabs').tabs({select: function(event, ui) {
		if(ui.index > getCurrentTabIndex()) {
			var thisTabValidates = tabValidates(getCurrentTab());
			if(thisTabValidates) {
				changeButtons(getButtonToTabMappings(), ui.index)
				if(thisTabValidates && $('.error-panel'))
					$('.error-panel').hide();
				$("#tab-" + getCurrentTabIndex() + ".ui-tabs-panel").find('input', 'textarea', 'textfield').first().focus();
			} else if(!thisTabValidates) {
				$('.error-panel').show();
			}
			return thisTabValidates;
		} else {
			changeButtons(getButtonToTabMappings(), ui.index);
			return true;
		}
	}});
}

function tabValidates(tab) {
	return tab.contentWidget('validate');
}

function changeButtons(buttonToTabMappings, tabIndex) {
	$.each(buttonToTabMappings, function(key, value) {
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

function getButtonToTabMappings() {
	return {
			"cancel" : range(0, getTabLength()),
			"prevPage": range(1, getTabLength()),
			"nextPage": range(0, getTabLength() - 1),
			"submit": [getTabLength()],
			"disabledBack": [0]
		}
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

function initializeTabContentWidgets() {
	for(i=0; i <= getTabLength(); i++) {
		$("#tabs-" + (i + 1)).contentWidget()
	}
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

$('.next').live('click', function() {
	if($(this).hasClass('disabled')) return;
	moveToRelativeTab(1);
});
	
$('.back').live('click', function() {
       moveToRelativeTab(-1);
});

$.widget("ui.contentWidget", {
	validate: function() {
		return this.options['validate'].call();                 
    },

    options: {validate: function() {return true;} }
});
