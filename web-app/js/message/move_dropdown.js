function moveAction() {
	var messageSection = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	if(messageSection == 'poll' || messageSection == 'folder' || messageSection == 'radioShow') {
		var location = url_root + "message/" + messageSection + "/" + ownerId;
	} else{
		var location = url_root + "message/" + messageSection;
	}
	var me = $('#move-actions option:selected');

	var messagesToMove = $('input:hidden[name=checkedMessageList]').val();
	
	if(me.hasClass('na')) return;
	if(me.hasClass('poll')) var section = 'poll';
	else if(me.hasClass('folder')) var section = 'folder';

	if(countCheckedMessages() > 1) {
		var move = 'moveAll';
		var messagesToMove = $('input:hidden[name=checkedMessageList]').val();
	} else {
		var move = 'move';
		var messagesToMove = $("#message-id").val();
	}

	$.ajax({
		type:'POST',
		url: url_root + 'message/' + move,
		data: {messageSection: section, messageId: messagesToMove, ownerId: me.val()},
		success: function(data) { window.location = location; }
	});
}
