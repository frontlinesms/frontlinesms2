function messageChecked(messageId) {
	var count = countCheckedMessages();
	var checkedMessageRow = $('#messages-table #message-' + messageId);
	
	if(checkedMessageRow.find('input[type=checkbox]').attr('checked')) {
		if(count == 1) {
			$('#messages-table').find('.selected').removeClass('selected');
			updateSingleCheckedDetails(messageId);
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
				updateSingleCheckedDetails(newMessageId);
			} else {
				removeFromChecked(messageId);
			}
		} else {
			clearCheckedList();
		}
	}
	setCheckAllBox(count);
}

function countCheckedMessages() {
    return $('input[name=message]:checked').size();
}

function updateSingleCheckedDetails(messageId){
	var messageSection = $('input:hidden[name=messageSection]').val();
	$.get(url_root + 'message/' + messageSection, { messageId: messageId }, function(data) {
		$('#message-details #single-message #message-info').html($(data).find('#message-details #single-message #message-info'));
		$('#message-details #single-message #other_btns').html($(data).find('#message-details #single-message #other_btns'));
		$('#message-details #single-message #poll-actions').html($(data).find('#message-details #single-message #poll-actions'));
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
	updateMultipleCheckedDetails(messageList);
}

function removeFromChecked(messageId) {
	var messageList = $('input:hidden[name=checkedMessageList]');
	var oldList = messageList.val();
	var newList = oldList.replace(','+ messageId +',', ',');
	messageList.val(newList);
}

function updateMultipleCheckedDetails(messageList) {
	var messageSection = $('input:hidden[name=messageSection]').val();
	alert(messageSection);
	$.get(url_root + 'message/' + messageSection, { messageId: 13, multipleMessages: messageList }, function(data) {
		alert('gettting');
	});
	alert('out');
}

function checkAll(){
	if($(':checkbox')[0].checked){
		$(':checkbox').each(function(index) {
			this.checked = true;
		});
		$('#messages-table tr').each(function(index) {
			$(this).addClass('selected');
			var messageId = $(this).attr('id').substring('message-'.length);
			addToChecked(messageId)
		});
	} else {
		$(':checkbox').each(function(index, element) {
			this.checked = false;
		});
		$('#messages-table tr').each(function(index) {
			$(this).removeClass('selected');
			var messageId = $(this).attr('id').substring('message-'.length);
			removeFromChecked(messageId)
		});
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

function clearCheckedList() {
	$('input:hidden[name=checkedMessageList]').val(',');
}

function getCheckedElements(element) {
	return $('input[name=' + element + ']:checked');
}

$('#btn_reply_all').live('click', function() {
	var me = $(this);
	var messageType = me.text();

	var recipients = []

	$.each(getSelectedGroupElements('message'), function(index, value) {
			var recipient = $("input:hidden[name=src-" + value.value + "]").val();
			if(isValid(recipient)) {
				recipients.push(recipient)
		}
	});

	$.ajax({
		type:'POST',
		traditional: true,
		data: {recipients: recipients, configureTabs: 'tabs-1, tabs-3'},
		url: url_root + 'quickMessage/create',
		success: function(data, textStatus){ launchMediumWizard(messageType, data, 'Send'); }
	});
});

$('#btn_delete_all').live('click', function() {
	var me = $(this);
	var messageType = me.text();
	var idsToDelete = []
	$.each(getSelectedGroupElements('message'), function(index, value) {
		if(isValid(value.value)) {
			idsToDelete.push(value.value)
		}
	});
	var messageSection = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	$.ajax({
		type:'POST',
		url: url_root + 'message/deleteMessage',
		traditional: true,
		context:'json',
		data: {messageSection: messageSection, ids: idsToDelete, ownerId: ownerId},
		success: function(data) { reloadPage(messageSection, ownerId)}
	});
});

$('#btn_archive_all').live('click', function() {
	var me = $(this);
	var messageType = me.text();
	var messageSection = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	var idsToArchive = []
	$.each(getSelectedGroupElements('message'), function(index, value) {
		if(isValid(value.value)) {
			idsToArchive.push(value.value)
		}
	});

	$.ajax({
		type:'POST',
		url: url_root + 'message/archiveMessage',
		traditional: true,
		data: {messageSection: messageSection, ids:idsToArchive, ownerId: ownerId},
		success: function(data, textStatus){ reloadPage(messageSection, ownerId)}
	});
});

function reloadPage(messageSection, ownerId) {
	var params = location.search;
	if(messageSection == 'poll' || messageSection == 'folder'){
		var url = "message/"+messageSection+"/"+ownerId + params;
	} else {
		var url = "message/"+messageSection + params;
	}
	window.location = url_root + url
}