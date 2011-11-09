function updateCount() {
	var value = $(this).val();
	var wordCount = value.length
	var messageStats = $(this).siblings("span.character-count")
	if(wordCount > 3000) {
		//prevent addition of new content to message
		$(this).val(value.substring(0, 3000));
	} else {
		$.get(url_root + 'message/getSendMessageCount', {message: value}, function(data) {
			messageStats.html(data);
		});
	}
}