$(document).ready(function() {
	$('#message-detail #multiple-messages').hide();
});

// TODO rename to messageCheckChanged()
function messageChecked(messageId) {
	var count = getCheckedMessageCount();
	var checkedMessageRow = getMessageRow(messageId);

	if(checkedMessageRow.find('input[type=checkbox]').attr('checked')) {
		if(count == 1) {
			$('#message-list tr.selected').removeClass('selected');
			updateSingleCheckedDetails(messageId, checkedMessageRow);
		} else {
			updateMultipleCheckedDetails();
		}
		checkedMessageRow.addClass('selected');
	} else {
		if(count != 0) {
			checkedMessageRow.removeClass('selected');
			count = setCheckAllBox(count);
			if (count == 1) {
				var newMessageRowId = $('#message-list tr.selected').attr('id');
				var newMessageId = newMessageRowId.substring('message-'.length);
				updateSingleCheckedDetails(newMessageId, getMessageRow(newMessageId));
			} else {
				updateMultipleCheckedDetails();
			}
		}
	}
	updateCheckAllBox(count);
}

function getMessageRow(messageId) {
	return $('#message-list tr#message-' + messageId);
}

function getCheckedMessageList() {
	var list=",";
	$('#message-list .message-select-checkbox:checked').each(function() {
		list += $(this).attr('id').substring('message-select-'.length) + ",";
	});
	return list;
}

function getCheckedMessageCount() {
    return $('#message-list .message-select-checkbox:checked').size();
}

function updateSingleCheckedDetails(messageId, row) {
	row.removeClass("unread");
	row.addClass("read");

	var params = { messageSection:$('input:hidden[name=messageSection]').val() };
	$.get(url_root + "message/show/" + messageId, params, function(data) {
		$('#multiple-messages').hide();
		var newPane = $(data);
		newPane.find('.dropdown').selectmenu();
		$('#single-message').replaceWith(newPane);
	});
}

function updateMultipleCheckedDetails() {
	// hide single message view
	$('#single-message').hide();
	// show multi message view
	$("#multiple-messages").show();
	// update counter display
	$('#checked-message-count').text(i18n("fmessage.selected.many", getCheckedMessageCount()));
}

function checkAll() {
	if($('#message-list :checkbox')[0].checked){
		var messageId;
		$('#message-list .message-preview :checkbox').each(function(index) {
			this.checked = true;
		});
		$('#message-list tr.message-preview').each(function(index) {
			$(this).addClass('selected');
		});
		if(getCheckedMessageCount() != 1) updateMultipleCheckedDetails(messageId);
	} else {
		$('#message-list tr.message-preview :checkbox').each(function(index, element) {
			this.checked = false;
		});
		$('#message-list tr.message-preview').each(function(index) {
			$(this).removeClass('selected');
		});
		var originalSingleMessageDisplay = $('#message-list tr.initial-selection');
		if(originalSingleMessageDisplay) originalSingleMessageDisplay.addClass('selected');
		$('#multiple-messages').hide();
		$('#single-message').show();
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
