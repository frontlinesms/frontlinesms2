function generateMessage(messageType){
	switch (messageType) {
		case "join_reply":
			$('input[name=joinReplyMessage]').val('Welcome');
			$('#join_reply_checkbox').attr('checked',true);
			break;
		case "leave_reply":
			$('input[name=leaveReplyMessage]').val('Bye');
			$('#leave_reply_checkbox').attr('checked',true);
			break;
		default:
			break;
	}
}