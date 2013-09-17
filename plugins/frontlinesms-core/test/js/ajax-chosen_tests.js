var counter;

QUnit.module("ajax-chosen", {
	setup:function() {
		console.log("ajax-chosen_tests.setup()");
		dom_trix.initDomFromFile("test/js/ajax-chosen/standard.html");
		timer.init(15000);
		ajaxChosen.init();
		url_root = "http://www.example.com/contextPath/";
		$.each($('select.chzn-select'), function(index, element) {
			$(element).ajaxChosen({
				url:url_root+"search/contactSearch",
				type:"POST",
				dataType:"json",
				minTermLength:1,
				keepTypingMsg:i18n("recipientSelector.keepTyping"),
				lookingForMsg:i18n("recipientSelector.searching"),
				sendSelectedSoFarOnEachLookup:true
			});
		});
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

test("If I don't do anything, nothing will go wrong", function() {
	// when
	timer.tick();

	// expect
	equal(0, 0);
});

/*
test("If I type stuff, things happen", function() {
	// when
	console.log("DEBUG ****** 1");
	console.log($('body').html());
	$('input[type=text]').val("testing").keyup();

	// expect
	console.log("DEBUG ****** 2");
	console.log($('body').html());
	equal(ajax_spy.requestCount(), 1);
});
*/
