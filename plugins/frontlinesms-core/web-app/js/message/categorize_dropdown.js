function categorizeClickAction(me) {
	var responseId, ownerId, messageSection, messagesToChange, successUrl;
	me = $(me).val();
	responseId = me.substring(4, me.length);
	ownerId = $("#owner-id").val();
	messageSection = $('input:hidden[name=messageSection]').val();
	if(getCheckedItemCount('interaction') > 1) {
		messagesToChange = getCheckedList('interaction');
		successUrl = "message/" + messageSection + "/" + ownerId;
	} else {
		messagesToChange = $("#interaction-id").val();
		successUrl = "message/" + messageSection + "/" + ownerId + "/show/" + messagesToChange;
	}

	// TODO replace pointless AJAX with normal <form/>
	$.ajax({
		type:'POST',
		url: url_root + 'message/changeResponse', // TODO move this to the poll controller
		data: {responseId: responseId, messageId: messagesToChange, ownerId: ownerId},
		success: function(data) { window.location = url_root + successUrl; }
	});
}
