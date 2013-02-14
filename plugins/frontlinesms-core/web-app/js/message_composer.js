messageComposerUtils = {
	getCharacterCount: function(messageComposerInstance) {
		return $($(messageComposerInstance).children('textarea')[0]).val().length;
	},

	getText: function(messageComposerInstance) {
		return $($(messageComposerInstance).children('textarea')[0]).val()
	},

	updateCharacterCount: function(messageComposerInstance) {
		var charCount, charCountDisplay, textArea, text;
		textArea = $(messageComposerInstance).children('textarea')
		// charCountDisplay = $($(messageComposerInstance)
		charCountDisplay = $($(messageComposerInstance).children('.controls')[0]).children('.character-count-display');
		console.log($(charCountDisplay));
		charCount = this.getCharacterCount($(messageComposerInstance))
		text = this.getText($(messageComposerInstance))
		$(charCountDisplay).html(charCount);
		if (text.indexOf('${') !== -1) {
			$(charCountDisplay).addClass('warning').attr('title', 'May be longer after substitution');
		} else {
			$(charCountDisplay).removeClass('warning').attr('title', 'OK');
		}
	}
}

$(function() {
	$('.message-composer').live('focus', function() {
		$(this).addClass('focus');
		$(this).children('textarea').autosize();
	});

	$('.message-composer').live('blur', function() {
		$(this).removeClass('focus');
	});

	$('.message-composer textarea').live('keyup', function() {
		messageComposerUtils.updateCharacterCount($($(this).parent()[0]));
	});
});

