$(document).ready(function() {
	$("#message-actions").change(moveAction);
});

function moveAction() {
	var count = countCheckedMessages();
	var checkedMessageList = '';
	var messageSection = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	if(messageSection == 'poll' || messageSection == 'folder'){
		var location = "/frontlinesms2/message/"+messageSection+"/"+ownerId;
	} else{
		var location = "/frontlinesms2/message/"+messageSection;
	}
	var me = $(this).find('option:selected');
	if(count > 1) {
		moveMultipleMessages(me, location);
		return;
	}

	var mesId = $("#message-id").val()
	if(me.hasClass('na')) return;
	if(me.hasClass('poll')) {
		var section = 'poll';
	} else if(me.hasClass('folder')) {
		var section = 'folder';
	}
	
	$.ajax({
		type:'POST',
		data: {messageSection: section, ids: mesId, ownerId: me.val()},
		url: '/frontlinesms2/message/move',
		success: function(data) {
			window.location = location;
		}
	});
}

function moveMultipleMessages(object, location) {
	var msgsToMove = []
	$.each(getSelectedGroupElements('message'), function(index, value) {
			msgsToMove.push(value.value)
	});

	if(object.hasClass('poll')) {
		var section = 'poll';
	} else if(object.hasClass('folder')) {
		var section = 'folder';
	}
	$.ajax({
		type:'POST',
		traditional: true,
		data: {messageSection: section, ownerId: object.val(), ids: msgsToMove},
		url: '/frontlinesms2/message/move',
		success: function(data) {
			window.location = location;
		}
	});
}
