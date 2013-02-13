$(function() {
	$('.message-composer').live('focus', function() {
		$(this).addClass('focus');
	});

	$('.message-composer').live('blur', function() {
		$(this).removeClass('focus');
	});

	$('.message-composer textarea').autosize();
});
