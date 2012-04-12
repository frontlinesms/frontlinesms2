$(document).ready(function() {
	$('#message-detail #multiple-messages').hide();
});

function messageChecked(messageId) {
	var count = countCheckedMessages();
	var checkedMessageRow = $('#message-list tr#message-' + messageId);
	
	if(checkedMessageRow.find('input[type=checkbox]').attr('checked')) {
		if(count == 1) {
			$('#message-list tr.selected').removeClass('selected');
			upSingleCheckedDetails(messageId);
		} else {
			addToChecked(messageId);
		}
		checkedMessageRow.addClass('selected');
	} else {
		if(count != 0) {
			checkedMessageRow.removeClass('selected');
			if (count == 1) {
				var newMessageRowId = $('#message-list tr.selected').attr('id');
				var newMessageId = newMessageRowId.substring('message-'.length);
				downSingleCheckedDetails(newMessageId);
			} else {
				removeFromChecked(messageId);
			}
		} else {
			$('input:hidden[name=checkedMessageList]').val(',');
		}
	}
	count = setCheckAllBox(count);
}

function countCheckedMessages() {
    return $('#message-list tr :checked').size();
}

function upSingleCheckedDetails(messageId) {
	updateMessageDetails(messageId, false);
	var messageList = $('input:hidden[name=checkedMessageList]');
	var newList = ',' + messageId + ',';
	messageList.val(newList);
}

function downSingleCheckedDetails(messageId) {
	updateMessageDetails(messageId, false);
	var messageList = $('input:hidden[name=checkedMessageList]');
	var newList = ',' + messageId + ',';
	messageList.val(newList);
}

function addToChecked(messageId) {
	var messageList = $('input:hidden[name=checkedMessageList]');
	var oldList = messageList.val();
	var newList = oldList + messageId + ',';
	messageList.val(newList);
	updateMultipleCheckedDetails(messageId);
}

function removeFromChecked(messageId) {
	var messageList = $('input:hidden[name=checkedMessageList]');
	var newList = jQuery.grep(messageList.val().split(","), function(element, index) {return element != messageId}).join(",");
	messageList.val(newList);
	updateMultipleCheckedDetails(messageId);
}

function updateMultipleCheckedDetails(messageId) {
	updateMessageDetails(messageId, true);
}

function updateMessageDetails(messageId, hasMultipleSelected) {
	var searchId = $('input:hidden[name=searchId]').val();
	var new_url;
	
	if(url.indexOf("show") >= 0)
		new_url = url.replace(/\d+\/$/, messageId);
	else
		new_url = url_root + controller + '/' + action;
	
	var params = { messageId: messageId, searchId: searchId}
	if($("#ownerId").val()) {
		params.ownerId = $("#ownerId").val();
	}
	if(hasMultipleSelected) {
		params.checkedMessageList = $("#checkedMessageList").val()
	}
	
	if(controller === "archive") {
		params.viewingMessages = true
	}
	$.get(new_url, params, function(data) {
		if(hasMultipleSelected === true) {
			$('#single-message').replaceWith($(data).find('#multiple-messages'));
			$('#multiple-messages').replaceWith($(data).find('#multiple-messages'));
		}
		if($('#multiple-messages').is(":visible") && !hasMultipleSelected) {
			$('#multiple-messages').replaceWith($(data).find('#single-message'));
		} else {
			$('#single-message').replaceWith($(data).find('#single-message'));
		}
		$("#message-detail .dropdown").selectmenu();
	});
}

function checkAll() {
	if($('#message-list :checkbox')[0].checked){
		var messageId;
		$('#message-list .message-preview :checkbox').each(function(index) {
			this.checked = true;
		});
		$('input:hidden[name=checkedMessageList]').val(",")
		$('#message-list tr.message-preview').each(function(index) {
			$(this).addClass('selected');
			messageId = $(this).attr('id').substring('message-'.length);
			var messageList = $('input:hidden[name=checkedMessageList]');
			var oldList = messageList.val();
			var newList = oldList + messageId + ',';
			messageList.val(newList);
		});
		if(countCheckedMessages() != 1) updateMultipleCheckedDetails(messageId);
	} else {
		$('#message-list tr.message-preview :checkbox').each(function(index, element) {
			this.checked = false;
		});
		$('#message-list tr.message-preview').each(function(index) {
			$(this).removeClass('selected');
		});
		var selectFirst = $('#message-list tr.message-preview').first();
		selectFirst.addClass('selected');
		var messageId = selectFirst.attr('id').substring('message-'.length);
		if(countCheckedMessages() == downSingleCheckedDetails(messageId));
		$('input:hidden[name=checkedMessageList]').val(',');
	}
}

function setCheckAllBox(count) {
	//Check whether all messages are checked
	if(count == $('#message-list tr.message-preview :checkbox').size() && !$('#message-list :checkbox')[0].checked){
		$('#message-list :checkbox')[0].checked = true;
	} else if($('#message-list :checkbox')[0].checked){
		$('#message-list :checkbox')[0].checked = false;
		count--;
	}
	return count;
}
