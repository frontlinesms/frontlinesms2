$(document).ready(function() {
	$('tr :checkbox[checked="true"]').parent().parent().addClass('checked');
});

function setStarStatus(object,data){
	if(data == 'starred') {
		$("#"+object).addClass(data);
		$("#"+object).removeClass("unstarred");
	} else {
		$("#"+object).addClass(data);
		$("#"+object).removeClass("starred");
	}	
}

var selectedMessageId;

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

function updateMessageDetails(id){
	highlightRow(id);

	var count = countCheckedMessages();
	if(count == 1 && $(':checkbox').size() != 2) {
		if(selectedMessageId != $('tr :checkbox[checked="true"]').val())
			loadMessage($('tr :checkbox[checked="true"]').val(), true);
	}
	if(count > 1){
		changeMessageCount(count);
	}
}

function loadMessage(id, checked) {
	var messageSection = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	var messageId = id;
	if(checked) {
		messageId = messageId+"?checkedId="+messageId;
	}
	
	if(ownerId != null && messageSection == 'poll' || messageSection == 'folder' || messageSection == 'radioShow') {
		window.location = url_root + 'message/'+messageSection+"/"+ownerId+"/show/"+messageId;
	} else if(ownerId != null && messageSection == 'result') {
		window.location = url_root + "search/"+messageSection+"/"+messageId+"&activityId="+$('input:hidden[name=activityId]').val()+"&groupId="+$('input:hidden[name=groupId]').val()+"&searchString="+$('input:hidden[name=searchString]').val();
	} else {
		window.location = url_root + 'message/'+messageSection+"/show/"+messageId;
	} 
}

function countCheckedMessages(){
     return validateCheckedMessageCount(getSelectedGroupElements('message').size())

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
	$('#message-details p#message-date').remove();
	$('#message-details p#message-body').remove();
	$('#message-details .button').remove();
	$("<p id='message-count'></p>").replaceAll($('#message-details p'));
	$('#message-details #message-count').empty().append(count+" messages selected");
	setMessageActions();
}

function setMessageActions() {
	var replyAll = '';
	var messageSection = $('input:hidden[name=messageSection]').val()
	if(messageSection != 'pending'){
		replyAll = "<a id='btn_reply_all' >Reply All</a>";
	}
	archiveAll = "<a id='btn_archive_all' >Archive All</a>";
	deleteAll = "<a id='btn_delete_all' >Delete All</a>";
	$('#message-details .buttons').empty();
	$('#message-details div.buttons').append(replyAll+"&nbsp;"+archiveAll+"&nbsp;"+deleteAll);
	
	$('#btn_reply_all').click(quickReplyClickAction);
	$('#btn_delete_all').click(deleteAllClickAction);
	$('#btn_archive_all').click(archiveClickAction);
}

function getSelectedGroupElements(groupName) {
	return $('input[name=' + groupName + ']:checked');
}

function quickReplyClickAction() {
	var me = $(this);
	var messageType = me.text();

	var recipients = []

	$.each(getSelectedGroupElements('message'), function(index, value) {
			recipients.push($("input:hidden[name=src-" + value.value + "]").val())
	});

	$.ajax({
		type:'POST',
		traditional: true,
		data: {recipients: recipients},
		url: url_root + 'quickMessage/create',
		success: function(data, textStatus){ launchMediumWizard(messageType, data, 'Send'); }
	});
}

function deleteAllClickAction() {
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
}

function archiveClickAction() {
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
}

function reloadPage(messageSection, ownerId) {
	if(messageSection == 'poll' || messageSection == 'folder'){
		var location = 'message/'+messageSection+"/"+ownerId;
	} else{
		var location = 'message/'+messageSection;
	}
	window.location = url_root + location
}
function setSelectedMessage() {
	if(selectedMessageId == null){
		selectedMessageId = $('tr.selected').attr('id').substring('message-'.length);
	}
}

function showMessageDetails() {
	if(selectedMessageId == null){
		return;
	}
	loadMessage(selectedMessageId, false);
}

function highlightRow(id){
	if( $('tr :checkbox[value='+id+']').attr('checked') == 'checked'){
		$("#message-"+id).addClass('checked')
	} else {
		$("#message-"+id).removeClass('checked')
	}
}

function isValid(value) {
		return value != "0"
}

