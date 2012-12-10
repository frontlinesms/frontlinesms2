test("a basic test example", function (assert) {
	ok(true, "this test is fine");
	var value = "hello";
	equal("hello", value, "We expect value to be hello");
});

test("recipientSelecter is initialiased", function() {
	notEqual(recipientSelecter, null, "recipientSelecter object should be defined");
	notEqual(recipientSelecter.addAddressHandler, null);
	notEqual(recipientSelecter.updateRecipientCount, null);
	notEqual(recipientSelecter.searchForContacts, null);
	notEqual(recipientSelecter.selectMembers, null);
	notEqual(recipientSelecter.setContact, null);
	notEqual(recipientSelecter.validateAddressEntry, null);

	equal(recipientSelecter.nonExistentMethod, null);
});

test("setContact should add a contact if he is not already there", function() {
	// given
	dom_trix.initDomFromString("<html><head></head><body>" +
			"<input type='text' value='' id='mobileNumbers'/>" +
			"<input type='checkbox' name='whatever' checked='checked'/>" +
			"<span id='recipient-count'>0</span>" +
			"</body></html>");
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

