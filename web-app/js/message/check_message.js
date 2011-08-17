$(document).ready(function() {
	$('tr :checkbox[checked="true"]').parent().parent().addClass('checked');
});

function messageChecked(messageId) {
	var count = countCheckedMessages();
	var checkedMessageRow = $('#messages-table #message-' + messageId);
	
	if(checkedMessageRow.find('input[type=checkbox]').attr('checked')) {
		if(count == 1) {
			$('#messages-table').find('.selected').removeClass('selected');
			updateMessageDetails(messageId);
		} else {
			multipleMessagesChecked(messageId);
		}
		checkedMessageRow.addClass('selected');
	} else {
		if(count != 0) {
			checkedMessageRow.removeClass('selected');
			if (count == 1) {
				var newMessageRowId = $('#messages-table').find('.selected').attr('id');
				var newMessageId = newMessageRowId.substring('message-'.length);
				updateMessageDetails(newMessageId);
			}
		}
	}
}

function updateMessageDetails(messageId){
	$.get(url_root + 'message/inbox', { messageId: messageId }, function(data) {
		$('#message-details #single-message #message-info').html($(data).find('#message-details #single-message #message-info'));
		$('#message-details #single-message #other_btns').html($(data).find('#message-details #single-message #other_btns'));
		$('#message-details #single-message #move-message').html($(data).find('#message-details #single-message #move-message'));
		$('#message-details #single-message #poll-actions').html($(data).find('#message-details #single-message #poll-actions'));
	});
}

function multipleMessagesChecked(messageId) {
	
}

function checkAllMessages(){
	if($(':checkbox')[0].checked){
		$(':checkbox').each(function(index){
			this.checked = true;
			if(index > 0){
				$(this).parent().parent().addClass('checked');
			}
		});

		changeMessageCount($(':checkbox').size()-1);
	} else {
		$(':checkbox').each(function(index, element){
			this.checked = false;
			if(index > 0)
				$(this).parent().parent().removeClass('checked');
		});
		showMessageDetails();
	}
}

function loadMessage(id, checked) {
	var url = $(".displayName-" + id).attr("href")
	if(checked == true){
	   url = url + (url.indexOf("?") > -1 ? "&" : "?")
		url = url + "checkedId="+id;
	}
	window.location = url
}

function countCheckedMessages(){
     return validateCheckedMessageCount(getSelectedGroupElements('message').size());
}

function validateCheckedMessageCount(count) {
	//Check whether all messages are checked
	if(count == $(':checkbox').size()-1 && !$(':checkbox')[0].checked){
			$(':checkbox')[0].checked = true;
		} else if($(':checkbox')[0].checked){
			$(':checkbox')[0].checked = false;
			count--;
		}
		return count;
}


function changeMessageCount(count){
	setSelectedMessage();
	$('#checked-message-count').html("<p> "+count+" messages selected</p>");
	setMessageActions();
}

function setMessageActions() {
	$('#multiple-message').show()
	$('#single-message').hide()
}

function getSelectedGroupElements(groupName) {
	return $('input[name=' + groupName + ']:checked');
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

function setSelectedMessage() {
	if(selectedMessageId == null){
		selectedMessageId = $('tr.selected').attr('id').substring('message-'.length);
	}
}

function enableSingleAction() {
	$('#multiple-message').hide()
	$('#single-message').show()
}

function showMessageDetails(){
	enableSingleAction();
}

function isValid(value) {
		return value && value != "0"
}
