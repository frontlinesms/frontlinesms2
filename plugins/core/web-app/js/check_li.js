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
	multipleDetails.find("input[type='submit']").each(function() { fsmsButton.apply(this); });
}

function itemCheckChanged(itemTypeString, itemId) {
	var count = getCheckedItemCount(itemTypeString);
	var checkedRow = getRow(itemTypeString, itemId);
	if(checkedRow.find('input[type=checkbox]').attr('checked')) {
		if(count == 1) {
			$('#' + itemTypeString + '-list .selected').removeClass('selected');
			updateSingleCheckedDetails(itemTypeString, itemId, checkedRow);
		} else {
			updateMultipleCheckedDetails(itemTypeString);
		}
		checkedRow.addClass('selected');
	} else {
		if(count != 0) {
			checkedRow.removeClass('selected');
			if (count == 1) {
				var newRowId = $('#' + itemTypeString + '-list .selected').attr('id');
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
	return $('#' + itemTypeString +'-list #' + itemTypeString + '-' + rowId);
}

function getCheckedList(itemTypeString) {
	var list=",";
	$('#' + itemTypeString + '-list .' + itemTypeString + '-select-checkbox:checked').each(function() {
		list += $(this).attr('id').substring(itemTypeString.length + '-select-'.length) + ",";
	});
	return list;
}

function getCheckedItemCount(itemTypeString) {
    return $('#' + itemTypeString + '-list .' + itemTypeString + '-select-checkbox:checked').size();
}

function updateSingleCheckedDetails(itemTypeString, itemId, row) {
	var params, action;
	if (itemTypeString == 'message') {
		row.removeClass("unread");
		row.addClass("read");
		params = { messageSection:$('input:hidden[name=messageSection]').val(), messageId: itemId };
		action = '/show/';
	} else {
		params = { contactsSection:$('input:hidden[name=contactsSection]').val() };
		action = '/updateContactPane/'
	}
	$.get(url_root + itemTypeString + action + itemId, params, function(data) {
		$('#multiple-'+itemTypeString+'s').hide();

		var newPane = $(data);
		newPane.find('.dropdown').selectmenu();
		newPane.find("input[type='submit']").each(function() { fsmsButton.apply(this); });
		$('#single-'+itemTypeString).replaceWith(newPane);
		if (itemTypeString == 'contact') {
			document.getElementById('new-field-dropdown-button').style.width="250px"; //fix width for custom field dropdown
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
			$('#multiple-'+itemTypeString+'s').replaceWith(pane);
			$('#checked-'+ itemTypeString + '-count').text(i18n("many.selected", getCheckedItemCount(itemTypeString), itemTypeString));
			applyContactPaneJavascriptEnhancements(pane);
		});
	} else {	
		// update counter display
		$('#checked-'+ itemTypeString + '-count').text(i18n("many.selected", getCheckedItemCount(itemTypeString), itemTypeString));
	}
}

function applyContactPaneJavascriptEnhancements(pane) {-
	pane.find("#group-list li a.remove-group").click(removeGroupClickAction);
	pane.find("#group-dropdown").change(addGroupClickAction);
	pane.find("#multi-group-dropdown").change(addGroupClickAction);
	pane.find("#multi-group-list li a.remove-group").click(removeGroupClickAction);
	pane.find("input[type='submit']").each(function() { fsmsButton.apply(this); });
}

function checkAll(itemTypeString) {
	if($('#' + itemTypeString + '-list :checkbox')[0].checked){
		$('#' + itemTypeString + '-list .' + itemTypeString + '-preview :checkbox').each(function(index) {
			this.checked = true;
		});
		$('#' + itemTypeString + '-list .' + itemTypeString + '-preview').each(function(index) {
			$(this).addClass('selected');
		});
		var checkedItemCount = getCheckedItemCount(itemTypeString);
		if(checkedItemCount == 1) {
			var tableRow = $("tr.message-preview:nth-child(2)");
			var id = tableRow.attr("id").substring(itemTypeString.length + 1);
			updateSingleCheckedDetails(itemTypeString, id, tableRow);
		} else updateMultipleCheckedDetails(itemTypeString);
	} else {
		$('#' + itemTypeString + '-list .' + itemTypeString + '-preview :checkbox').each(function(index, element) {
			this.checked = false;
		});
		$('#' + itemTypeString + '-list .' + itemTypeString + '-preview').each(function(index) {
			$(this).removeClass('selected');
		});
		var originalSingleItemDisplay = $('#' + itemTypeString + '-list .initial-selection');
		if(originalSingleItemDisplay) originalSingleItemDisplay.addClass('selected');
		$('#multiple-' + itemTypeString + 's').hide();
		$('#single-' + itemTypeString).show();
	}
}

function updateCheckAllBox(count) {
	// Check whether all messages are checked
	if(count == $('#message-list tr.message-preview :checkbox').size() && !$('#message-list :checkbox')[0].checked) {
		$('#message-list :checkbox')[0].checked = true;
	} else if($('#message-list :checkbox')[0].checked) {
		$('#message-list :checkbox')[0].checked = false;
	}
}

