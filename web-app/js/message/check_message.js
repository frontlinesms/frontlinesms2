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
	setCheckAllBox(count);
}

function countCheckedMessages() {
    return $('input[name=message]:checked').size();
}

function upSingleCheckedDetails(messageId){
	var messageSection = $('input:hidden[name=messageSection]').val();
	$.get(url_root + 'message/' + messageSection, { messageId: messageId }, function(data) {
		$('#message-details #single-message #message-info').replaceWith($(data).find('#message-details #single-message #message-info'));
		$('#message-details #single-message #other_btns').replaceWith($(data).find('#message-details #single-message #other_btns'));
		$('#message-details #single-message #poll-actions').replaceWith($(data).find('#message-details #single-message #poll-actions'));
	});
	var messageList = $('input:hidden[name=checkedMessageList]');
	var newList = ',' + messageId + ',';
	messageList.val(newList);
}

function downSingleCheckedDetails(messageId){
	var messageSection = $('input:hidden[name=messageSection]').val();
	$.get(url_root + 'message/' + messageSection, { messageId: messageId }, function(data) {
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
	var oldList = messageList.val();
	var newList = oldList.replace(','+ messageId +',', ',');
	messageList.val(newList);
	updateMultipleCheckedDetails(messageId);
}

function updateMultipleCheckedDetails(messageId) {
	var messageSection = $('input:hidden[name=messageSection]').val();
	$.get(url_root + 'message/' + messageSection, { messageId: messageId, checkedMessageList: $("#checkedMessageList").val() }, function(data) {
		$('#message-details #single-message').replaceWith($(data).find('#message-details #multiple-messages').show());
		$('#message-details #multiple-messages').replaceWith($(data).find('#message-details #multiple-messages').show());
	});
}

function checkAll(){
	if($(':checkbox')[0].checked){
		var messageId;
		$(':checkbox').each(function(index) {
			this.checked = true;
		});
		$('#messages-table tr').each(function(index) {
			$(this).addClass('selected');
			messageId = $(this).attr('id').substring('message-'.length);
			var messageList = $('input:hidden[name=checkedMessageList]');
			var oldList = messageList.val();
			var newList = oldList + messageId + ',';
			messageList.val(newList);
		});
		updateMultipleCheckedDetails(messageId);
	} else {
		$(':checkbox').each(function(index, element) {
			this.checked = false;
		});
		$('#messages-table tr').each(function(index) {
			$(this).removeClass('selected');
		});
		var selectFirst = $('#messages-table tr').first();
		selectFirst.addClass('selected');
		var messageId = selectFirst.attr('id').substring('message-'.length);
		downSingleCheckedDetails(messageId);
		$('input:hidden[name=checkedMessageList]').val(',');
	}
}

function setCheckAllBox(count) {
	//Check whether all messages are checked
	if(count == $(':checkbox').size()-1 && !$(':checkbox')[0].checked){
		$(':checkbox')[0].checked = true;
	} else if($(':checkbox')[0].checked){
		$(':checkbox')[0].checked = false;
		count--;
	}
}