function moveAction() {
	var messageSection = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	var searchId = $("input:hidden[name=searchId]").val();
	
	var me = $('#move-actions option:selected');

	var messagesToMove = $('input:hidden[name=checkedMessageList]').val();
	
	if(me.hasClass('na')) return;
	var section = me.attr("class");

	if(countCheckedMessages() > 1) {
		var messagesToMove = $('input:hidden[name=checkedMessageList]').val();
	} else {
		var messagesToMove = $("#message-id").val();
	}
	
	if(messageSection == 'result' && !(countCheckedMessages() > 1)) {
		var location = url_root + "search/" + messageSection + '/' + messagesToMove + '?searchId=' + searchId;
	} else if(messageSection == 'result') {
		var location = url_root + "search/" + messageSection + '?searchId=' + searchId;
	} else if(messageSection == 'activity' || messageSection == 'folder' || messageSection == 'radioShow') {
		var location = url_root + "message/" + messageSection + "/" + ownerId;
	} else{
		var location = url_root + "message/" + messageSection;
	}
	
	$.ajax({
		type:'POST',
		url: url_root + 'message/move',
		data: {messageSection: section, messageId: messagesToMove, ownerId: me.val()},
		success: function(data) { window.location = location; }
	});
}
