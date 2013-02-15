QUnit.module("message_composer");

function setUp() {
	dom_trix.initDomFromFile("test/js/message_composer/message_composer_test.html");
	window.messageComposerUtils = messageComposerUtils;
}

test("messageComposerUtils.getCharacterCount should return the number of characters in the textarea of a messageComposer", function() {
	var messageComposerInstance = $('.message-composer')[0],
	charCount = messageComposerUtils.getCharacterCount($(messageComposerInstance));
	equal(charCount, 17);
});
