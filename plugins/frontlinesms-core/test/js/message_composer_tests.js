QUnit.module("message_composer");

function setup() {
	dom_trix.initDomFromFile("test/js/message_composer/standard.html");
	window.messageComposerUtils = messageComposerUtils;
}

test("messageComposerUtils.getCharacterCount should return the number of characters in the textarea of a message composer", function() {
	setup();
	var messageComposerInstance = $('.message-composer')[0],
	charCount = messageComposerUtils.getCharacterCount($(messageComposerInstance));
	equal(charCount, 17);
});

test("messageComposerUtils.getText should return the text contained in the textarea of a message composer", function() {
	setup();
	var messageComposerInstance = $('.message-composer')[0],
	messageText = messageComposerUtils.getText($(messageComposerInstance));
	equal(messageText, "This is a message");

});

test("messageComposerUtils.updateCharacterCount should update character counter to reflect change in associated textarea", function() {
	setup();
	var messageComposerTextarea= $($('.message-composer')[0]).children('textarea')[0];
	messageComposerInstance = $('.message-composer')[0],
	$(messageComposerTextarea).val("This is a message, edited.");
	charCount = messageComposerUtils.getCharacterCount($(messageComposerInstance));
	equal(charCount, 26);
});
