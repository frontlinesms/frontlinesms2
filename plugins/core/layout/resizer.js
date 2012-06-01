var Resizer = function(listId, listHeaderId, listFooterId) {
	var _main_header_height = $("#head").height(),
		_list_header = $("#" + listHeaderId),
		_list_footer = $("#" + listFooterId),
		_list = $("#" + listId),
		_do_resize = function() {
			var headerHeight = _list_header.height(),
				footerHeight = _list_footer.height();
			_list.css('top', headerHeight + _main_header_height);
			_list.css('bottom', footerHeight);
		};
	return { go: _do_resize };
};

