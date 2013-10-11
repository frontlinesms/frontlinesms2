QUnit.module('recipient_selecter');

function setup() {
	dom_trix.initDomFromFile('test/js/recipient_selecter/standard.html');
	window.recipientSelecter = recipientSelecter;
}

test('validateDeferred() should return true as long as a recipient, empty or not, is selected', function() {
	setup();
	$('[name=recipients]').val('group-1');
	ok(recipientSelecter.validateDeferred());
});

test('fetchRecipientCount() should update the value returned by getRecipientCount()', function() {
	url_root = "http://www.example.com/contextPath/";
	setup();
	$('[name=recipients]').val('group-1');
	equal(recipientSelecter.getRecipientCount(), 0)
	ajax_spy.init({recipientCount: 123});
	recipientSelecter.fetchRecipientCount();
	equal(recipientSelecter.getRecipientCount(), 123);
});

test('confirmation screen should have updated values when fetchRecipientCount() is called', function() {
	url_root = "http://www.example.com/contextPath/";
	setup();
	$('[name=recipients]').val('group-1');
	ajax_spy.init({recipientCount: 1030});
	recipientSelecter.fetchRecipientCount();
	equal($('#contacts-count').html(), 1030);
	equal($('#messages-count').html(), 1030);
});
