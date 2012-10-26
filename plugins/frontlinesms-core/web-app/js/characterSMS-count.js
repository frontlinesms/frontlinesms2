function updateSmsCharacterCount() {
	var textArea, messageText, messageStats;
	textArea = $(this);
	messageText = $(this).val();
	messageStats = textArea.parent().find("span.character-count");
	if(messageText.length > 3000) {
		//prevent addition of new content to message
		$(this).val(messageText.substring(0, 3000));
	} else {
		$.get(url_root + 'message/sendMessageCount', { message:messageText }, function(data) {
			messageStats.text(i18n('message.character.count', data.remaining, data.partCount));
			if(messageText.indexOf("${") !== -1)
			{
				messageStats.addClass("invalid");
				textArea.parent().find(".character-count-warning").show();
			}
			else
			{
				messageStats.removeClass("invalid");
				textArea.parent().find(".character-count-warning").hide();
			}
		});
	}
}

