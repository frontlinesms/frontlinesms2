$(document).ready(function() {
	$('#message-detail #multiple-messages').hide();
	$('#contact-editor #multiple-contacts').hide();
});

function showMultipleDetailsPanel(itemTypeString) {
	// hide single message view
	$('#single-'+itemTypeString).hide();

	// show multi message view
	var multipleDetails = $("#multiple-"+itemTypeString+'s');
	multipleDetails.show();
	fsmsButton.findAndApply("input[type='submit']", multipleDetails);
	multipleDetails.find(".dropdown").selectmenu("destroy");
	multipleDetails.find(".dropdown").selectmenu();
}

function itemCheckChanged(itemTypeString, itemId) {
	var count = getCheckedItemCount(itemTypeString);
	var checkedRow = getRow(itemTypeString, itemId);
	if(checkedRow.find('input[type=checkbox]').attr('checked')) {
		if(count == 1) {
			$('#main-list .selected').removeClass('selected');
			updateSingleCheckedDetails(itemTypeString, itemId, checkedRow);
		} else {
			updateMultipleCheckedDetails(itemTypeString);
		}
		checkedRow.addClass('selected');
	} else {
		if(count != 0) {
			checkedRow.removeClass('selected');
			if (count == 1) {
				var newRowId = $('#main-list .selected').attr('id');
				var newId = newRowId.substring(itemTypeString.length + 1);
				updateSingleCheckedDetails(itemTypeString, newId, getRow(itemTypeString, newRowId));
			} else {
				updateMultipleCheckedDetails(itemTypeString);
			}
		}
	}
	
	if (itemTypeString == 'message')
		updateCheckAllBox(count);
}

function getRow(itemTypeString, rowId) {
	return $('#main-list #' + itemTypeString + '-' + rowId);
}

function getCheckedList(itemTypeString) {
	var list=",";
	$('#main-list .' + itemTypeString + '-select-checkbox:checked').each(function() {
		list += $(this).attr('id').substring(itemTypeString.length + '-select-'.length) + ",";
	});
	return list;
}

function getCheckedItemCount(itemTypeString) {
    return $('#main-list .' + itemTypeString + '-select-checkbox:checked').size();
}

function updateSingleCheckedDetails(itemTypeString, itemId, row) {
	var params, action;
	if (itemTypeString == 'message') {
		row.removeClass("unread");
		row.addClass("read");
		params = { messageSection:$('input:hidden[name=messageSection]').val(), messageId: itemId, ownerId: $('input:hidden[name=ownerId]').val()};
		action = '/show/';
	} else {
		params = { contactsSection:$('input:hidden[name=contactsSection]').val() };
		action = '/updateContactPane/'
	}
	$.get(url_root + itemTypeString + action + itemId, params, function(data) {
		$('#multiple-'+itemTypeString+'s').hide();
		var newPane = $(data);
		fsmsButton.findAndApply("input[type='submit']", newPane);
		$('#single-'+itemTypeString).replaceWith(newPane);
		newPane.find('.dropdown').selectmenu();
		if (itemTypeString == 'contact') {
			applyContactPaneJavascriptEnhancements(newPane);
		}
		if (itemTypeString == 'message') {
			refreshMessageCount();
		}
	});
}

function updateMultipleCheckedDetails(itemTypeString) {
	showMultipleDetailsPanel(itemTypeString);
	if (itemTypeString == 'contact') {
		$.get(url_root + itemTypeString + "/multipleContactGroupList/", {checkedContactList: getCheckedList(itemTypeString)}, function(data) {
			var pane = $(data);
			pane.show(); // Pane is initially display:hidden in GSP
			$('#multiple-'+itemTypeString+'s').replaceWith(pane);
			$('#checked-'+ itemTypeString + '-count').text(i18n("many.selected", getCheckedItemCount(itemTypeString), itemTypeString));
			applyContactPaneJavascriptEnhancements(pane);
		});
	} else {	
		// update counter display
		$('#checked-'+ itemTypeString + '-count').text(i18n("many.selected", getCheckedItemCount(itemTypeString), itemTypeString));
	}
}

function applyContactPaneJavascriptEnhancements(pane) {
	initContactPaneGroups();
	initContactPaneFields();
	$("div.single-contact").keyup(function(event) {
		enableSaveAndCancel();
	});
}

function checkAll(itemTypeString) {
	if($('#main-list :checkbox')[0].checked){
		$('#main-list .' + itemTypeString + '-preview :checkbox').each(function(index) {
			this.checked = true;
		});
		$('#main-list .' + itemTypeString + '-preview').each(function(index) {
			$(this).addClass('selected');
		});
		var checkedItemCount = getCheckedItemCount(itemTypeString);
		if(checkedItemCount == 1) {
			var tableRow = $("tr.message-preview:nth-child(2)");
			var id = tableRow.attr("id").substring(itemTypeString.length + 1);
			updateSingleCheckedDetails(itemTypeString, id, tableRow);
		} else updateMultipleCheckedDetails(itemTypeString);
	} else {
		$('#main-list .' + itemTypeString + '-preview :checkbox').each(function(index, element) {
			this.checked = false;
		});
		$('#main-list .' + itemTypeString + '-preview').each(function(index) {
			$(this).removeClass('selected');
		});
		var originalSingleItemDisplay = $('#main-list .initial-selection');
		if(originalSingleItemDisplay) originalSingleItemDisplay.addClass('selected');
		$('#multiple-' + itemTypeString + 's').hide();
		$('#single-' + itemTypeString).show();
	}
}

function updateCheckAllBox(count) {
	// Check whether all messages are checked
	if(count == $('#main-list tr.message-preview :checkbox').size() && !$('#message-list :checkbox')[0].checked) {
		$('#main-list :checkbox')[0].checked = true;
	} else if($('#main-list :checkbox')[0].checked) {
		$('#main-list :checkbox')[0].checked = false;
	}
}

