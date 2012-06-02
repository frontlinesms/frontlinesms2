var Resizer = function(listId, listHeaderId, listFooterId) {
	var main_header_height = $("#head").height(),
		list_header = $("#" + listHeaderId),
		list_footer = $("#" + listFooterId),
		list = $("#" + listId),
		list_left = list.css("left"),
		list_right = list.css("right"),
		do_resize = function() {
			list.css('top', list_header.height() + main_header_height);
			list.css('bottom', list_footer.height());
		};

	// initialise
	list_header.css("position", "fixed");
	list_header.css("top", main_header_height);
	list_header.css("left", list_left);
	list_header.css("right", list_right);

	list_footer.css("position", "fixed");
	list_footer.css("bottom", 0);
	list_footer.css("left", list_left);
	list_footer.css("right", list_right);

	do_resize();

	return do_resize;
};

