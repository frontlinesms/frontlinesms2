function moveAction() {
	var messageSection = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	if(messageSection == 'result') {
		var location = url_root + "search/" + messageSection;
	} else if(messageSection == 'poll' || messageSection == 'folder' || messageSection == 'radioShow') {
		var location = url_root + "message/" + messageSection + "/" + ownerId;
	} else{
		var location = url_root + "message/" + messageSection;
	}
	var me = $('#move-actions option:selected');

	var messagesToMove = $('input:hidden[name=checkedMessageList]').val();
	
	if(me.hasClass('na')) return;
	if(me.hasClass('inbox')) var section = 'inbox';
	if(me.hasClass('poll')) var section = 'poll';
	else if(me.hasClass('folder')) var section = 'folder';

	if(countCheckedMessages() > 1) {
		var messagesToMove = $('input:hidden[name=checkedMessageList]').val();
	} else {
		var messagesToMove = $("#message-id").val();
	}

	$.ajax({
		type:'POST',
		url: url_root + 'message/move',
		data: {messageSection: section, messageId: messagesToMove, ownerId: me.val()},
		success: function(data) { window.location = location; }
	});
}
