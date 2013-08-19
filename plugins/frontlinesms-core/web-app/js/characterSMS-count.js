function updateSmsCharacterCount() {
	var textArea, messageText, messageStats;
	textArea = $(this);
	messageText = textArea.val();
	messageStats = textArea.parent().find("span.character-count");
	if(messageText.length > 3000) {
		//prevent addition of new content to message
		textArea.val(messageText.substring(0, 3000));
	} else {
		$.get(url_root + 'message/sendMessageCount', { message:messageText }, function(data) {
			var countWarning = textArea.parent().find(".character-count-warning");
			messageStats.text(i18n('message.character.count', data.remaining, data.partCount));
			if(messageText.indexOf("${") !== -1) {
				messageStats.addClass("invalid");
				countWarning.show();
			} else {
				messageStats.removeClass("invalid");
				countWarning.hide();
			}
		});
	}
}

