messageComposerUtils = (function() {
	var init, getCharacterCount, getText, updateCharacterCount;

	init = function(messageComposerParent) {
		$(messageComposerParent).on('focus', '.message-composer', function(event) {
			$(this).addClass('focus');
			$(this).children('textarea').autosize();
		});

		$(messageComposerParent).on('blur', '.message-composer', function(event) {
			$(this).removeClass('focus');
		});

		$(messageComposerParent).on('keyup', '.message-composer textarea', function() {
			messageComposerUtils.updateCharacterCount($($(this).parent()[0]));
		});
	};

	getCharacterCount = function(messageComposer) {
		return $($(messageComposer).children('textarea')[0]).val().length;
	};

	getText = function(messageComposer) {
		return $($(messageComposer).children('textarea')[0]).val();
	};

	updateCharacterCount = function(messageComposer) {
		var charCount, charCountDisplay, textArea, text;
		textArea = $(messageComposer).children('textarea');
		charCountDisplay = $($(messageComposer).children('.controls')[0]).children('.character-count-display');
		charCount = this.getCharacterCount($(messageComposer));
		text = this.getText($(messageComposer));
		$(charCountDisplay).html(charCount);
		if (text.indexOf('${') !== -1) {
			$(charCountDisplay).addClass('warning').attr('title', 'May be longer after substitution');
		} else {
			$(charCountDisplay).removeClass('warning').attr('title', 'OK');
		}
	};

	return {
		init: init,
		getCharacterCount: getCharacterCount,
		getText: getText,
		updateCharacterCount: updateCharacterCount
	};
}());
