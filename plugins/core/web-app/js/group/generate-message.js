function generateMessage(messageType){
	switch (messageType) {
		case "join_reply":
			$('input[name=joinReplyMessage]').val(i18n("group.join.reply.message"));
			$('#join_reply_checkbox').attr('checked',true);
			break;
		case "leave_reply":
			$('input[name=leaveReplyMessage]').val(i18n("group.leave.reply.message"));
			$('#leave_reply_checkbox').attr('checked',true);
			break;
		default:
			break;
	}
}
