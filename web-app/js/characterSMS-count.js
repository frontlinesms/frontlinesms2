function updateCount() {
	var value = $(this).val();
	var messageStats = $(this).siblings("#message-stats")
	if(value.length > 3000) {
		//prevent addition of new content to message
		$(this).val(value.substring(0, 3000));
	} else if(value.length > 59) {
		$.get(url_root + 'message/getSendMessageCount', {message: value}, function(data) {
			messageStats.html(value.length + " characters " + data);
		});
	} else {
		messageStats.html(value.length + " characters (1 SMS message)");
	}
}