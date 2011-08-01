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
				addMessageIdToList(this.value);
			}
		});
		
		changeMessageCount($(':checkbox').size()-1);
	} else {
		$(':checkbox').each(function(index, element){
			this.checked = false;
			if(index > 0)			
				$(this).parent().parent().removeClass('checked');
		});
		emptyMessageList();
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
	var location = window.location.pathname
	var messageSection = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	var messageId = id;
	if(checked == true){
		messageId = messageId+"?checkedId="+messageId;
	}
	
	
	if(ownerId != null && messageSection == 'poll' || messageSection == 'folder' || messageSection == 'radioShow') {
		window.location = "/frontlinesms2/message/"+messageSection+"/"+ownerId+"/show/"+messageId;
	} else{
		window.location = "/frontlinesms2/message/"+messageSection+"/show/"+messageId;
	}
}

function countCheckedMessages(){
	var count = 0;
	$(':checkbox').each(function(){
			if(this.checked){
				count++;
				addMessageIdToList(this.value);
			} else{
				removeMessageIdFromList(this.value);
			}
		});
	
	return validateCheckedMessageCount(count);
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
	$('#message-details').empty();
	$('#message-details').append("<p> "+count+" messages selected</p>");
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
	$('#message-details').append("<div class='buttons'></div>");
	$('#message-details div.buttons').append(replyAll+"&nbsp;"+archiveAll+"&nbsp;"+deleteAll);
	$('#btn_reply_all').click(quickReplyClickAction);
	$('#btn_delete_all').click(deleteAllClickAction);
	$('#btn_archive_all').click(archiveClickAction);
}

function quickReplyClickAction() {
	var me = $(this);
	var messageType = me.text();
	var checkedMessageIdList = $('input:hidden[name=checkedMessageIdList]').val();

	$.ajax({
		type:'POST',
		data: {checkedMessageIdList: checkedMessageIdList},
		url: '/frontlinesms2/quickMessage/create',
		success: function(data, textStatus){ launchWizard(messageType, data); }
	});
}

function deleteAllClickAction() {
	var me = $(this);
	var messageType = me.text();
	var checkedMessageIdList = $('input:hidden[name=checkedMessageIdList]').val();
	var messageSection = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	$.ajax({
		type:'POST',
		context:document.body,
		data: {messageSection: messageSection, checkedMessageIdList: checkedMessageIdList, ownerId: ownerId},
		url: '/frontlinesms2/message/deleteMessage',
		success: function(data) { reloadPage(messageSection, ownerId)}
	});
}

function archiveClickAction() {
	var me = $(this);
	var messageType = me.text();
	var messageSection = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	var checkedMessageIdList = $('input:hidden[name=checkedMessageIdList]').val();
	
	$.ajax({
		type:'POST',
		data: {messageSection: messageSection, checkedMessageIdList: checkedMessageIdList, ownerId: ownerId},
		url: '/frontlinesms2/message/archiveMessage',
		success: function(data, textStatus){ reloadPage(messageSection, ownerId)}
	});
}

function reloadPage(messageSection, ownerId) {
	if(messageSection == 'poll' || messageSection == 'folder'){
		var location = "/frontlinesms2/message/"+messageSection+"/"+ownerId;
	} else{
		var location = "/frontlinesms2/message/"+messageSection;
	}
	window.location = location
}
function setSelectedMessage() {
	if(selectedMessageId == null){
		selectedMessageId = $('tr.selected').attr('id').substring('message-'.length);
	}
}

function showMessageDetails(){
	if(selectedMessageId == null){
		return;
	}
	loadMessage(selectedMessageId,false);
}

function highlightRow(id){
	if( $('tr :checkbox[value='+id+']').attr('checked') == 'checked'){
		$("#message-"+id).addClass('checked')
	} else {
		$("#message-"+id).removeClass('checked')
	}
}

function removeMessageIdFromList(id) {
	var f = $('input:hidden[name=checkedMessageIdList]');
	var oldList = f.val().replace(0 +',', ",");
	if(oldList.indexOf(id + ',') != -1) {
		var newList = oldList.replace(id +',', ",");
			f.val(newList);
	}
}

function addMessageIdToList(id) {
	var f = $('input:hidden[name=checkedMessageIdList]');
	var oldList = f.val();
	
	if(oldList.indexOf(id + ',') == -1 && id != 0) {
		var newList;
		newList = oldList + id + ',';
		f.val(newList);
	}
}

function emptyMessageList() {
	var f = $('input:hidden[name=checkedMessageList]');
	f.val("");
}
