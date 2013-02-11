QUnit.module("app_info");

function setup() {
	fakeTimer.init();
}

test("Listeners set without explicit frequency should fire with frequency=1", function() {
	TODO();
});

test("Listeners set with explicit frequency should fire only with requested frequency", function() {
	TODO();
});

test("Interested data with implicit frequency should be requested on every call", function() {
	TODO();
});

test("Interested data with explicit frequency should only be requested when necessary", function() {
	TODO();
});

test("Multiple requests for the same data should throw an exception", function() {
	// N.B. we may want to support this at a later date.  At that time, behaviour
	// around differing frequencies etc. should be defined.
	TODO();
});

