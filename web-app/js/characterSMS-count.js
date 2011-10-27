function updateCount() {
	var value = $(this).val();
	
	if(value.length > 3000) {
		//prevent addition of new content to message
		$(this).val(value.substring(0, 3000));
	} else if(value.length > 140) {
		$.get(url_root + 'message/getSendMessageCount', {message: value}, function(data) {
			$(this).siblings("#message-stats").html(value.length + " characters " + data);
		});
	} else {
		$(this).siblings("#message-stats").html(value.length + " characters (1 SMS message)");
	}
}