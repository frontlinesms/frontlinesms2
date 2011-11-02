function updateCount() {
	var value = $(this).val();
	var wordCount = value.length
	var messageStats = $(this).siblings("#message-stats")
	if(wordCount > 3000) {
		//prevent addition of new content to message
		$(this).val(value.substring(0, 3000));
	} else if(wordCount >= 160) {
		$.get(url_root + 'message/getSendMessageCount', {message: value}, function(data) {
			messageStats.html(wordCount + " characters " + data);
		});
	} else {
		messageStats.html(wordCount + " characters (1 SMS message)");
	}
}