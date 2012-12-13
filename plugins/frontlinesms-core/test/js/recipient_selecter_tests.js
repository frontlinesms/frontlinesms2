function setup() {
	dom_trix.initDomFromFile("test/js/recipient_selecter/standard.html");

	// Make recipientSelecter visible to jsdom
	window.recipientSelecter = recipientSelecter;

	notEqual(recipientSelecter, null, "recipientSelecter object should be defined");
	notEqual(recipientSelecter.addAddressHandler, null);
	notEqual(recipientSelecter.updateRecipientCount, null);
	notEqual(recipientSelecter.searchForContacts, null);
	notEqual(recipientSelecter.selectMembers, null);
	notEqual(recipientSelecter.setContact, null);
	notEqual(recipientSelecter.validateAddressEntry, null);

	equal(recipientSelecter.nonExistentMethod, null);
}

function checkGroup(elementNumber) {
	var inputElement = $("#groups-" + elementNumber);
	inputElement.attr("checked", "checked");
	recipientSelecter.selectMembers(inputElement, inputElement.val(), inputElement.next().text(), eval(inputElement.attr("groupmembers")));
}

function checkContact(elementNumber) {
	var e = $("#addresses-" + elementNumber);
	e.attr("checked", "checked");
	recipientSelecter.setContact(e, e.val());
}

function assertRecipientCountEquals(expectedCount) {
	equal(jQuery("#recipient-count").text(), expectedCount, "recipient count check");
}

test("validate should fail if no mobile number, contact, group or smart group selected", function() {
	// given
	setup();

	// then
	equal(false, recipientSelecter.validateImmediate());
	equal(false, recipientSelecter.validateDeferred());
});

test("validate should pass if address is present in address field", function() {
	// given
	setup();

	// when
	$("#address").val("+123");

	// then
	ok(recipientSelecter.validateImmediate());
	ok(recipientSelecter.validateDeferred());
});

test("validate should pass if address is added from address field", function() {
	// given
	setup();
	$("#address").val("+123");

	// when
	recipientSelecter.addAddressHandler();

	// then
	ok(recipientSelecter.validateImmediate());
	ok(recipientSelecter.validateDeferred());
});

test("validate should pass if single contact is selected", function() {
	// given
	setup();

	// when
	checkContact(0);

	// then
	ok(recipientSelecter.validateImmediate());
	ok(recipientSelecter.validateDeferred());
});

test("validate should pass if populated group is selected", function() {
	// given
	setup();

	// when
	checkGroup(5);

	// then
	assertRecipientCountEquals(2);
	ok(recipientSelecter.validateImmediate(), "should validate");
	ok(recipientSelecter.validateDeferred(), "should validate");
});

test("validateImmediate should not pass if only empty groups are selected, but validateDeferred should", function() {
	// given
	setup();

	// when
	checkGroup(0);

	// then
	assertRecipientCountEquals(0);
	equal(false, recipientSelecter.validateImmediate());
	ok(recipientSelecter.validateDeferred());
});

test("setContact should add a contact if he is not already there", function() {
	// given
	setup();
	var e = jQuery("input[name=whatever]");

	// when
	recipientSelecter.setContact(e, "+123456");

	// then
	equal(jQuery("#mobileNumbers").val(), "+123456", "Mobile number should now be included in list");
	equal(jQuery("#recipient-count").text(), 1, "Should have one recipient.");

	// when
	recipientSelecter.setContact(e, "+7890");

	// then
	equal(jQuery("#mobileNumbers").val(), "+123456,+7890", "Mobile number should now be included in list");
	equal(jQuery("#recipient-count").text(), 2, "Should have one recipient.");

	// when
	recipientSelecter.setContact(e, "+123456");

	// then
	equal(jQuery("#mobileNumbers").val(), "+123456,+7890", "Mobile number should now be included in list");
	equal(jQuery("#recipient-count").text(), 2, "Should have one recipient.");

	// when
	e.removeAttr("checked");
	recipientSelecter.setContact(e, "+123456");

	// then
	equal(jQuery("#mobileNumbers").val(), "+7890", "Mobile number should now be included in list");
	equal(jQuery("#recipient-count").text(), 1, "Should have one recipient.");
});

