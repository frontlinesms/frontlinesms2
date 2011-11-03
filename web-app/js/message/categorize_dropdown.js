function categorizeClickAction() {
	var me = $('#categorise_dropdown option:selected').val();
	var responseId = me.substring(4, me.length);
	var ownerId = $("#owner-id").val();
	var messageSection = $('input:hidden[name=messageSection]').val();
	if(countCheckedMessages() > 1) {
		var messagesToChange = $('input:hidden[name=checkedMessageList]').val();
	} else {
		var messagesToChange = $("#message-id").val();
	}
	$.ajax({
		type:'POST',
		url: url_root + 'message/changeResponse',
		data: {responseId: responseId, messageId: messagesToChange, ownerId: ownerId},
		success: function(data) { window.location = url_root + "message/" + messageSection + "/" + ownerId; }
	});
}