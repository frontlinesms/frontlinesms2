// DOM setup
var dom_trix = (function() {
	var jsdom;
	try {
		console.log("Loading JSDOM...");
		jsdom = require('jsdom');
		console.log("JSDOM loaded.");
	} catch(err) {
		console.log("Error trying to load JSDOM: " + err);
		process.exit(1);
	}

	var initDomFromString, initDomFromFile, initJquery, resetDom;
	initDomFromString = function(domString) {
		console.log("Initialising DOM with HTML: " + domString + "...");
		var document = jsdom.jsdom(domString, null, { features: { QuerySelector: true } }),
			window = document.createWindow(),
			navigator = {
				userAgent: 'node-js'
			};
		global.window = window;
		global.navigator = navigator;
		global.document = window.document;
		console.log("DOM SETUP COMPLETE");
		initJquery();
	};
	initDomFromFile = function(file) {
		var fs = require('fs');
		initDomFromString(fs.readFileSync(file).toString());
	};
	initJquery = function() {
		console.log("Initialising jQuery...");
		var jQuery = require("jquery");
		global.jQuery = jQuery;
		global.$ = jQuery;
		console.log("jQuery initialiased.");
	};
	resetDom = function() {
		// TODO
	};
	return {
		initDomFromString:initDomFromString,
		initDomFromFile:initDomFromFile,
		resetDom:resetDom
	};
}());

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
	dom_trix.initDomFromString("<html><head></head><body><input type='text' value='' id='mobileNumbers'/><span id='recipient-count'>0</span></body></html>");
	var e = jQuery("<input>", {type:"checkbox"});

	// when
	recipientSelecter.setContact(e, "+123456");

	// then
	equal(jQuery("#mobileNumbers").val(), ",+123456", "Mobile number should now be included in list");
	equal(jQuery("#recipient-count").text(), 1, "Should have one recipient.");
});

