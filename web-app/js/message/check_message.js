$(document).ready(function() {
	$('#message-details #multiple-messages').hide();
});

function messageChecked(messageId) {
	var count = countCheckedMessages();
	var checkedMessageRow = $('#messages-table #message-' + messageId);
	
	if(checkedMessageRow.find('input[type=checkbox]').attr('checked')) {
		if(count == 1) {
			$('#messages-table').find('.selected').removeClass('selected');
			upSingleCheckedDetails(messageId);
		} else {
			addToChecked(messageId);
		}
		checkedMessageRow.addClass('selected');
	} else {
		if(count != 0) {
			checkedMessageRow.removeClass('selected');
			if (count == 1) {
				var newMessageRowId = $('#messages-table').find('.selected').attr('id');
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
    return $('#messages-table tr :checked').size();
}

function upSingleCheckedDetails(messageId) {
	var messageSection = $('input:hidden[name=messageSection]').val();
	if (messageSection == 'result') {
		var url = 'search/result';
	} else {
		var url = 'message/' + messageSection;
	}
	var searchId = $('input:hidden[name=searchId]').val() || '';
	var ownerId = $('input:hidden[name=ownerId]').val();
	$.get(url_root + url, { messageId: messageId, ownerId: ownerId, searchId: searchId}, function(data) {
		$('#message-details #message-id').replaceWith($(data).find('#message-details #message-id'));
		$('#message-details #message-src').replaceWith($(data).find('#message-details #message-src'));
		$('#message-details #single-message #message-info').replaceWith($(data).find('#message-details #single-message #message-info'));
		$('#message-details #single-message #other_btns').replaceWith($(data).find('#message-details #single-message #other_btns'));
		$('#message-details #single-message #poll-actions').replaceWith($(data).find('#message-details #single-message #poll-actions'));
	});
	var messageList = $('input:hidden[name=checkedMessageList]');
	var newList = ',' + messageId + ',';
	messageList.val(newList);
}

function downSingleCheckedDetails(messageId) {
	var messageSection = $('input:hidden[name=messageSection]').val();
	if (messageSection == 'result') {
		var url = 'search/result';
	} else {
		var url = 'message/' + messageSection;
	}
	var ownerId = $('input:hidden[name=ownerId]').val();
	var isArchived = $('input:hidden[name=isArchived]').val();
	var searchId = $('input:hidden[name=searchId]').val() || '';
	$.get(url_root + url, { messageId: messageId, ownerId: ownerId, archived: isArchived, searchId: searchId}, function(data) {
		$('#message-details #multiple-messages').replaceWith($(data).find('#message-details #single-message'));
	});
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
	var messageSection = $('input:hidden[name=messageSection]').val();
	if (messageSection == 'result') {
		var url = 'search/result';
	} else {
		var url = 'message/' + messageSection;
	}
	var ownerId = $('input:hidden[name=ownerId]').val();
	var isArchived = $('input:hidden[name=isArchived]').val();
	var searchId = $('input:hidden[name=searchId]').val() || '';
	$.get(url_root + url, { messageId: messageId, ownerId: ownerId, checkedMessageList: $("#checkedMessageList").val(), archived: isArchived, searchId: searchId}, function(data) {
		$('#message-details #single-message').replaceWith($(data).find('#message-details #multiple-messages'));
		$('#message-details #multiple-messages').replaceWith($(data).find('#message-details #multiple-messages'));
	});
}

function checkAll() {
	if($('#messages :checkbox')[0].checked){
		var messageId;
		$('#messages-table tr :checkbox').each(function(index) {
			this.checked = true;
		});
		$('input:hidden[name=checkedMessageList]').val(",")
		$('#messages-table tr').each(function(index) {
			$(this).addClass('selected');
			messageId = $(this).attr('id').substring('message-'.length);
			var messageList = $('input:hidden[name=checkedMessageList]');
			var oldList = messageList.val();
			var newList = oldList + messageId + ',';
			messageList.val(newList);
		});
		if(countCheckedMessages() != 1) updateMultipleCheckedDetails(messageId);
	} else {
		$('#messages-table tr :checkbox').each(function(index, element) {
			this.checked = false;
		});
		$('#messages-table tr').each(function(index) {
			$(this).removeClass('selected');
		});
		var selectFirst = $('#messages-table tr').first();
		selectFirst.addClass('selected');
		var messageId = selectFirst.attr('id').substring('message-'.length);
		if(countCheckedMessages() == downSingleCheckedDetails(messageId));
		$('input:hidden[name=checkedMessageList]').val(',');
	}
}

function setCheckAllBox(count) {
	//Check whether all messages are checked
	if(count == $('#messages-table tr :checkbox').size() && !$('#messages :checkbox')[0].checked){
		$('#messages :checkbox')[0].checked = true;
	} else if($('#messages :checkbox')[0].checked){
		$('#messages :checkbox')[0].checked = false;
		count--;
	}
	return count;
}
