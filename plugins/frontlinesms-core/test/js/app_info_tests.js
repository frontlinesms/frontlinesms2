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
				++called;
			};
			return {
				called:function() { return called; },
				func:func
			};
		}());

		emptyFunc = function() {};
	}
});

test("If no listeners are set, no call should be made", function() {
	// when
	timer.tick();

	// expect
	equal(ajax_spy.requestCount(), 0);
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
	// given
	var i;
	app_info.listen("asdf", counter.func);

	// when
	timer.tick(3);

	// then
	equal(ajax_spy.requestCount(), 3);

	for(i=2; i>=0; --i) {
		ok(ajax_spy.getRequest(i).data.hasOwnProperty("asdf"));
	}
});

test("Interested data with explicit frequency should only be requested when necessary", function() {
	// given
	app_info.listen("ignore", emptyFunc);
	app_info.listen("asdf", 2, counter.func);

	// when
	timer.tick(3);

	// then
	equal(ajax_spy.requestCount(), 3);

	ok(!ajax_spy.getRequest(0).data.hasOwnProperty("asdf"));
	ok(ajax_spy.getRequest(1).data.hasOwnProperty("asdf"));
	ok(!ajax_spy.getRequest(2).data.hasOwnProperty("asdf"));
});

test("Passing data with a request should result in it being sent with the request", function() {
	// given
	app_info.listen("a", { x:1, y:2 }, counter.func);

	// when
	timer.tick();

	// then
	equal(ajax_spy.getRequest(0).data.a.x, 1);
	equal(ajax_spy.getRequest(0).data.a.y, 2);
});

test("Interested data with explicit frequency and data should only be requested when necessary", function() {
	// given
	app_info.listen("ignore", emptyFunc);
	app_info.listen("a", 2, [1, 2, 3], counter.func);

	// when
	timer.tick(3);

	// then
	equal(ajax_spy.requestCount(), 3);

	ok(!ajax_spy.getRequest(0).data.hasOwnProperty("a"));
	deepEqual(ajax_spy.getRequest(1).data.a, [1, 2, 3]);
	ok(!ajax_spy.getRequest(2).data.hasOwnProperty("a"));
});

test("Passing dataBuilder function with a request should result in it being sent with the request", function() {
	// given
	var builder = function() { return { x:1, y:2 }; };
	app_info.listen("a", builder, counter.func);

	// when
	timer.tick();

	// then
	deepEqual(ajax_spy.getRequest(0).data.a, { x:1, y:2 });
});

test("Interested data with explicit frequency and dataBuilder should only be requested when necessary", function() {
	// given
	var builder = function() { return [1, 2, 3]; };
	app_info.listen("ignore", emptyFunc);
	app_info.listen("a", 2, builder, counter.func);

	// when
	timer.tick(3);

	// then
	equal(ajax_spy.requestCount(), 3);

	ok(!ajax_spy.getRequest(0).data.hasOwnProperty("a"));
	deepEqual(ajax_spy.getRequest(1).data.a, [1, 2, 3]);
	ok(!ajax_spy.getRequest(2).data.hasOwnProperty("a"));
});

test("Multiple requests for the same data should throw an exception", function() {
	// N.B. we may want to support this at a later date.  At that time, behaviour
	// around differing frequencies etc. should be defined.
	// given
	app_info.listen("a", function() {});

	// expect
	raises(function() {
		app_info.listen("a", function() {});
	});
});

test("Calling stopListening when there is no registered listener should not throw an exception", function() {
	// when
	app_info.stopListening("ixxa");

	// then nothing happens
	ok(true);
});

test("Calling stopListening when there is a registered listener will prevent it from being called", function() {
	//given
	app_info.listen("a", counter.func);

	// when
	timer.tick();

	// then
	equal(counter.called(), 1);

	// when
	app_info.stopListening("a");
	timer.tick();

	// then there's no change in the count
	equal(counter.called(), 1);
});

