function setup() {
	dom_trix.initDomFromFile("test/js/check_list/standard.html");

	window.check_list = check_list;

	equal(true, true);
}

test("updateCheckAllBox should not trigger all-checked box when none checked", function() {
	// given
	setup();

	// when
	check_list.updateCheckAllBox(0);

	// then
	equal(false, $($('#main-list :checkbox')[0]).is(":checked"));
});

test("updateCheckAllBox should trigger all-checked box when all checked", function() {
	// given
	setup();
	$(":checkbox:not(:first-child)").attr("checked", "checked");

	// when
	check_list.updateCheckAllBox(5);

	// then
	equal(true, $($('#main-list :checkbox')[0]).is(":checked"));
});

