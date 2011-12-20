$(document).ready(function() {
	$('#message-detail #multiple-messages').hide();
});

function messageChecked(messageId) {
	var count = countCheckedMessages();
	var checkedMessageRow = $('#message-list tbody #message-' + messageId);
	
	if(checkedMessageRow.find('input[type=checkbox]').attr('checked')) {
		if(count == 1) {
			$('#message-list tbody').find('.selected').removeClass('selected');
			upSingleCheckedDetails(messageId);
		} else {
			addToChecked(messageId);
		}
		checkedMessageRow.addClass('selected');
	} else {
		if(count != 0) {
			checkedMessageRow.removeClass('selected');
			if (count == 1) {
				var newMessageRowId = $('#message-list tbody').find('.selected').attr('id');
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
    return $('#message-list tbody tr :checked').size();
}

function upSingleCheckedDetails(messageId) {
	var messageSection = $('input:hidden[name=messageSection]').val();
	var searchId = $('input:hidden[name=searchId]').val() || '';
	var ownerId = $('input:hidden[name=ownerId]').val();
	var viewingArchive = $('input:hidden[name=viewingArchive]').val() || false;
	if (messageSection == 'result') {
		var url = 'search/result';
	} else if(viewingArchive == "true" && ownerId){
		var url = 'archive/' + messageSection + '/' + ownerId + '/show/' + messageId;
	} else if(viewingArchive == "true") {
		var url = 'archive/' + messageSection;
	} else if (messageSection == 'radioShow') {
		var url = 'radioShow/' + ownerId;
	} else {
		var url = 'message/' + messageSection;
	}
	$.get(url_root + url, { messageId: messageId, ownerId: ownerId, searchId: searchId, viewingArchive: Boolean(viewingArchive)}, function(data) {
		$('#single-message').replaceWith($(data).find('#single-message'));
	});
	var messageList = $('input:hidden[name=checkedMessageList]');
	var newList = ',' + messageId + ',';
	messageList.val(newList);
}

function downSingleCheckedDetails(messageId) {
	var messageSection = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	var searchId = $('input:hidden[name=searchId]').val() || '';
	var viewingArchive = $('input:hidden[name=viewingArchive]').val() || false;
	if (messageSection == 'result') {
		var url = 'search/result';
	} else if(viewingArchive == "true" && ownerId){
		var url = 'archive/' + messageSection + '/' + ownerId + '/show/' + messageId;
	} else if(viewingArchive == "true") {
		var url = 'archive/' + messageSection;
	} else if(messageSection == "radioShow") {
		var url = messageSection + '/' + ownerId + '/show/' + messageId;
	} else {
		var url = 'message/' + messageSection;
	}
	
	$.get(url_root + url, { messageId: messageId, ownerId: ownerId, viewingArchive: viewingArchive, searchId: searchId}, function(data) {
		$('#multiple-messages').replaceWith($(data).find('#single-message'));
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
	var ownerId = $('input:hidden[name=ownerId]').val();
	var searchId = $('input:hidden[name=searchId]').val();
	var viewingArchive = $('input:hidden[name=viewingArchive]').val() || false;
	if (messageSection == 'result') {
		var url = 'search/result';
	} else if(viewingArchive == "true" && ownerId){
		var url = 'archive/' + messageSection + '/' + ownerId + '/show/' + messageId;
	} else if(viewingArchive == "true") {
		var url = 'archive/' + messageSection;
	} else if(messageSection == "radioShow") {
		var url = messageSection + '/' + ownerId + '/show/' + messageId;
	} else {
		var url = 'message/' + messageSection;
	}
	$.get(url_root + url, { messageId: messageId, ownerId: ownerId, checkedMessageList: $("#checkedMessageList").val(), viewingArchive: viewingArchive, searchId: searchId}, function(data) {
		$('#single-message').replaceWith($(data).find('#multiple-messages'));
		$('#multiple-messages').replaceWith($(data).find('#multiple-messages'));
	});
}

function checkAll() {
	if($('#messages thead :checkbox')[0].checked){
		var messageId;
		$('#message-list tbody tr :checkbox').each(function(index) {
			this.checked = true;
		});
		$('input:hidden[name=checkedMessageList]').val(",")
		$('#message-list tbody tr').each(function(index) {
			$(this).addClass('selected');
			messageId = $(this).attr('id').substring('message-'.length);
			var messageList = $('input:hidden[name=checkedMessageList]');
			var oldList = messageList.val();
			var newList = oldList + messageId + ',';
			messageList.val(newList);
		});
		if(countCheckedMessages() != 1) updateMultipleCheckedDetails(messageId);
	} else {
		$('#message-list tr :checkbox').each(function(index, element) {
			this.checked = false;
		});
		$('#message-list tr').each(function(index) {
			$(this).removeClass('selected');
		});
		var selectFirst = $('#message-list tbody tr').first();
		selectFirst.addClass('selected');
		var messageId = selectFirst.attr('id').substring('message-'.length);
		if(countCheckedMessages() == downSingleCheckedDetails(messageId));
		$('input:hidden[name=checkedMessageList]').val(',');
	}
}

function setCheckAllBox(count) {
	//Check whether all messages are checked
	if(count == $('#message-list tbody tr :checkbox').size() && !$('#messages :checkbox')[0].checked){
		$('#messages :checkbox')[0].checked = true;
	} else if($('#messages :checkbox')[0].checked){
		$('#messages :checkbox')[0].checked = false;
		count--;
	}
	return count;
}
