function categorizeClickAction() {
	var me = $('#categorise_dropdown option:selected').val();
	var responseId = me.substring(4, me.length);
	var ownerId = $("#owner-id").val();
	var messageSection = $('input:hidden[name=messageSection]').val();
	if(getCheckedItemCount('message') > 1) {
		var messagesToChange = getCheckedList('message');
		var successUrl = "message/" + messageSection + "/" + ownerId;
	} else {
		var messagesToChange = $("#message-id").val();
		var successUrl = "message/" + messageSection + "/" + ownerId + "/show/" + messagesToChange;
	}

	// TODO replace pointless AJAX with normal <form/>
	$.ajax({
		type:'POST',
		url: url_root + 'message/changeResponse', // TODO move this to the poll controller
		data: {responseId: responseId, messageId: messagesToChange, ownerId: ownerId},
		success: function(data) { window.location = url_root + successUrl; }
	});
}
