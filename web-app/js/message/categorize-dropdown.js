function categoriseClickAction(responseId) {
	var ownerId = $("#owner-id").val();
	var messageSection = $('input:hidden[name=messageSection]').val();
	alert('hello');
	if(countCheckedMessages() > 1) {
		alert('2ish');
		var change = 'changeAllResponses';
		var messagesToChange = $('input:hidden[name=checkedMessageList]').val();
	} else {
		var change = 'changeResponse';
		var messagesToChange = $("#message-id").val();
	}

	$.ajax({
		type:'POST',
		url: url_root + 'message/' + change,
		data: {responseId: responseId, messageId: messagesToChange, ownerId: ownerId},
		success: function(data) { window.location = url_root + "message/" + messageSection + "/" + ownerId; }
	});
}