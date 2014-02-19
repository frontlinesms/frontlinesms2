check_list = (function() {
	var checkAll, itemCheckChanged, showMultipleDetailsPanel, updateCheckAllBox, toggleCheckboxesEnabled,
			tick = 0;

	showMultipleDetailsPanel = function(itemTypeString) {
		// hide single message view
		$('#single-'+itemTypeString).hide();

		// show multi message view
		var multipleDetails = $("#multiple-"+itemTypeString+'s');
		multipleDetails.show();
		fsmsButton.findAndApply("input[type='submit']", multipleDetails);
		if (itemTypeString === "contact") {
			selectmenuTools.refresh("#multi-group-dropdown");
		} else {
			selectmenuTools.refresh("#multiple-interactions #move-actions");
			selectmenuTools.refresh("#multiple-interactions #categorise_dropdown");
		}
	};

	itemCheckChanged = function(itemTypeString, itemId, itemSubtypeString) {
		var count, checkedRow, newRowId, newId;
		count = getCheckedItemCount(itemTypeString);
		checkedRow = getRow(itemTypeString, itemId);
		if(checkedRow.find('input[type=checkbox]').attr('checked')) {
			if(count === 1) {
				$('#main-list .selected').removeClass('selected');
				updateSingleCheckedDetails(itemTypeString, itemId, checkedRow, itemSubtypeString);
			} else {
				updateMultipleCheckedDetails(itemTypeString, itemSubtypeString);
			}
			checkedRow.addClass('selected');
		} else if(count !== 0) {
			checkedRow.removeClass('selected');
			if (count === 1) {
				newRowId = $('#main-list .selected').attr('id');
				newId = newRowId.substring(itemTypeString.length + 1);
				updateSingleCheckedDetails(itemTypeString, newId, getRow(itemTypeString, newRowId), itemSubtypeString);
			} else {
				updateMultipleCheckedDetails(itemTypeString, itemSubtypeString);
			}
		}
		updateCheckAllBox(count);
		displayMoveactionDropdown();
	};

	getRow = function(itemTypeString, rowId) {
		return $('#main-list #' + itemTypeString + '-' + rowId);
	};

	getCheckedList = function(itemTypeString) {
		var list=",";
		$('#main-list .' + itemTypeString + '-select-checkbox:checked').each(function() {
			list += $(this).attr('id').substring(itemTypeString.length + '-select-'.length) + ",";
		});
		return list;
	};

	getCheckedItemCount = function(itemTypeString) {
	    return $('#main-list .' + itemTypeString + '-select-checkbox:checked').size();
	};

	toggleCheckboxesEnabled = function(itemTypeString, enabled) {
		if(enabled) {
			$('#main-list .' + itemTypeString + '-select-checkbox,#' + itemTypeString + '-select-all').removeAttr('disabled');
		}
		else {
			$('#main-list .' + itemTypeString + '-select-checkbox,#' + itemTypeString + '-select-all').attr('disabled', 'disabled');
		}
	};

	updateSingleCheckedDetails = function(itemTypeString, itemId, row, itemSubtypeString) {
		var params, action, singleDetails, callerTick, controller;
		if (itemTypeString === 'interaction') {
			row.removeClass("unread");
			row.addClass("read");
			params = { messageSection:$('input:hidden[name=messageSection]').val(), interactionId: itemId, ownerId: $('input:hidden[name=ownerId]').val()};
			action = '/show/';
			controller = itemSubtypeString; 
		} else {
			params = { contactsSection:$('input:hidden[name=contactsSection]').val() };
			action = '/updateContactPane/';
			controller = itemSubtypeString;
		}
		$("#multiple-"+itemTypeString+"s").hide();

		singleDetails = $("#single-" + itemTypeString);
		singleDetails.html("<p class='loading'>" + i18n("content.loading") + "</p>");
		singleDetails.show();

		callerTick = ++tick;
		toggleCheckboxesEnabled(itemTypeString, false);
		$.get(url_root + controller + action + itemId, params, function(data) {
			var newPane = $(data);
			if(callerTick !== tick) {
				return;
			}
			fsmsButton.findAndApply("input[type='submit']", newPane);
			singleDetails.replaceWith(newPane);
			newPane.find('.dropdown').selectmenu();
			if (itemTypeString === 'contact') {
				applyContactPaneJavascriptEnhancements(newPane);
			} else if (itemTypeString === 'interaction') {
				// TODO if message was unread and we're in the inbox, please decrement unread messages count
				// on the tab (e.g. MESSAGES (123) -> MESSAGES (122)
			}
			toggleCheckboxesEnabled(itemTypeString, true);
		});
	};

	updateMultipleCheckedDetails = function(itemTypeString, itemSubtypeString) {
		if (itemTypeString === 'contact') {
			toggleCheckboxesEnabled(itemTypeString, false);
			$.post(url_root + itemTypeString + "/multipleContactGroupList/", {checkedContactList: getCheckedList(itemTypeString)}, function(data) {
				var pane = $(data);
				pane.show(); // Pane is initially display:hidden in GSP
				$('#multiple-'+itemTypeString+'s').replaceWith(pane);
				$('#checked-'+ itemTypeString + '-count').text(i18n("many.selected", getCheckedItemCount(itemTypeString), itemTypeString));
				applyContactPaneJavascriptEnhancements(pane);
				showMultipleDetailsPanel(itemTypeString);
				toggleCheckboxesEnabled(itemTypeString, true);
			});
		} else {
			// update counter display
			$('#checked-'+ itemTypeString + '-count').text(i18n(itemSubtypeString + ".multiple.selected", getCheckedItemCount(itemTypeString)));
			showMultipleDetailsPanel(itemTypeString);
		}
	};

	applyContactPaneJavascriptEnhancements = function(pane) {
		contactEditor.init();
		groupMembershipEditor.init();
		$("#mobile").trigger('change');
	};

	checkAll = function(itemTypeString, itemSubtypeString) {
		var checkedItemCount, tableRow, id, originalSingleItemDisplay;
		if($('#main-list :checkbox')[0].checked){
			$('#main-list .' + itemTypeString + '-preview :checkbox').each(function(index) {
				this.checked = true;
			});
			$('#main-list .' + itemTypeString + '-preview').each(function(index) {
				$(this).addClass('selected');
			});
			checkedItemCount = getCheckedItemCount(itemTypeString);
			if(checkedItemCount === 1) {
				tableRow = $("#main-list tbody tr:first-child");
				id = tableRow.attr("id").substring(itemTypeString.length + 1);
				updateSingleCheckedDetails(itemTypeString, id, tableRow, itemSubtypeString);
			} else { updateMultipleCheckedDetails(itemTypeString, itemSubtypeString); }
		} else {
			$('#main-list .' + itemTypeString + '-preview :checkbox').each(function(index, element) {
				this.checked = false;
			});
			$('#main-list .' + itemTypeString + '-preview').each(function(index) {
				$(this).removeClass('selected');
			});
			originalSingleItemDisplay = $('#main-list .initial-selection');
			if(originalSingleItemDisplay) { originalSingleItemDisplay.addClass('selected'); }
			$('#multiple-' + itemTypeString + 's').hide();
			$('#single-' + itemTypeString).show();
		}
	};

	updateCheckAllBox = function(count) {
		// Check whether all messages are checked
		if(count !== 0 && count === $('#main-list tbody tr :checkbox').size() && !$('#main-list :checkbox')[0].checked) {
			$('#main-list :checkbox')[0].checked = true;
		} else if(count !== 0 && count === $('#main-list li:not(:first-child) input:checkbox').size() && !$('#main-list li input:checkbox')[0].checked) {
			$('#main-list :checkbox')[0].checked = true;
		} else if($('#main-list :checkbox')[0].checked) {
			$('#main-list :checkbox')[0].checked = false;
		}
	};

	displayMoveactionDropdown = function() {
		var checkedBoxes, multiple_moveActions, single_moveActions, showDropdown;
		showDropdown = false;
		checkedBoxes = $('#main-list .message-select-checkbox:checked');
		checkedBoxes.each(function(){
			showDropdown = (!$(this).parent().parent().hasClass("archived") || showDropdown);
		});
		single_moveActions = $("div#single-interaction a#move-actions-button");
		multiple_moveActions = $("div#multiple-interactions a#move-actions-button");
		if(checkedBoxes.size() > 1) {
			if(showDropdown){
				multiple_moveActions.show();
			} else {
				multiple_moveActions.hide();
			}
		}
	};

	return {
		checkAll:checkAll,
		itemCheckChanged:itemCheckChanged,
		updateCheckAllBox:updateCheckAllBox
	};
}());

