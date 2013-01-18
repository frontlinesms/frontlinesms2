fail = function(message) {
	console.log("Explicit call to fail with message: " + message);
	ok(false, message);
};

TODO = function(message) {
	console.log("TODO: " + message);
	if(message === undefined) {
		fail();
	} else {
		test("TODO: " + message, function() { fail(); });
	}
};

