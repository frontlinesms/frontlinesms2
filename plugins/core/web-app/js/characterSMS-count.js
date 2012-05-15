function updateSmsCharacterCount() {
	var messageText = $(this).val();
	var messageStats = $(this).siblings("span.character-count");
	if(messageText.length > 3000) {
		//prevent addition of new content to message
		$(this).val(messageText.substring(0, 3000));
	} else {
		$.get(url_root + 'message/sendMessageCount', { message:messageText }, function(data) {
			messageStats.text(i18n('message.character.count', data['remaining'], data['partCount']));
		});
	}
}
