var Resizer = function(container_selecter, fixed_header_selecter, fixed_footer_selecter) {
	// TODO
	// * for speed, could:
	//   - cache jQuery objects _fixed_headers and _fixed_footers
	//   - set width and position CSS only on first init
	// * does not currently behave well for table headings as column widths break for the header when it is removed from normal flow
	var
		_main_header_height = $("#head").outerHeight(), // this height is fixed
		_getBorders = function(element) {
			return {
				top:{ width:element.css("border-top-width"), style:element.css("border-top-style"), color:element.css("border-top-color") },
				right:{ width:element.css("border-right-width"), style:element.css("border-right-style"), color:element.css("border-right-color") },
				bottom:{ width:element.css("border-bottom-width"), style:element.css("border-bottom-style"), color:element.css("border-bottom-color") },
				left:{ width:element.css("border-left-width"), style:element.css("border-left-style"), color:element.css("border-left-color") }};
		},
		_setBorders = function(element, borders) {
			for(direction in borders) {
				var values = borders[direction];
				for(key in values) {
					element.css("border-" + direction + "-" + key, values[key]);
				}
			}
		},
		_fixed_headers = $(fixed_header_selecter),
		_fixed_footers = $(fixed_footer_selecter),
		_container = $(container_selecter);
		_resize = function() {
			var _header_offset = _main_header_height;
			var _container_left = _container.css("left");   // FF gives these values in px instead of % so
			var _container_right = _container.css("right"); // we have to recalculate every resize
			var _container_borders = _getBorders(_container);
			_fixed_headers.each(function(i, element) {
				element = $(element);
				element.css("position", "fixed");
				element.css("top", _header_offset);
				element.css("left", _container_left);
				element.css("right", _container_right);
				element.css("width", "auto");
				_setBorders(element, _container_borders);
				_header_offset += element.outerHeight();
			});

			var _footer_offset = 0;
			_fixed_footers.each(function(i, element) {
				element = $(element);
				element.css("position", "fixed");
				element.css("bottom", _footer_offset);
				element.css("left", _container_left);
				element.css("right", _container_right);
				element.css("width", "auto");
				_setBorders(element, _container_borders);
				_footer_offset += element.outerHeight();
			});
			_container.css('top', _header_offset);
			_container.css('bottom', _footer_offset);
		};
	_resize();
	return _resize;
};

