var counter;

QUnit.module("app_info", {
	setup:function() {
		console.log("app_info_tests.setup()");
		dom_trix.initEmptyDom();
		timer.init(15000);
		app_info.init();
		url_root = "http://www.example.com/contextPath/";
		ajaxSpy = ajax_spy.init();

		counter = (function() {
			var called = 0,
			func = function() {
				console.log("counter.func() :: calledPrevious=" + called);
				++called;
			};
			return {
				called:function() { return called; },
				func:func
			};
		}());
	}
});

test("Listeners set without explicit frequency should fire with frequency=1", function() {
	// given
	app_info.listen("asdf", counter.func);

	// when
	timer.tick(5);

	// then
	equal(counter.called(), 5);
});

test("Listeners set with explicit frequency should fire only with requested frequency", function() {
	// given
	app_info.listen("asdf", 3, counter.func);

	// when
	timer.tick(5);

	// then
	equal(counter.called(), 1);

	// when
	timer.tick();

	// then
	equal(counter.called(), 2);

	// when
	timer.tick(3);

	// then
	equal(counter.called(), 3);
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

