var routing = function() {
	var
	init = function () {
		$('input[name^="routeRule"]').change(handleChange);
		handleChange();
	},
	handleChange = function() {
		var checkedBoxCount = $('input[name^="routeRule"]:checked').size(),
		totalBoxCount = $('input[name^="routeRule"]').size();
		showOrHideWarning(totalBoxCount > 0 && checkedBoxCount == 0);
	},
	showOrHideWarning = function(hasError) {
		var warningElement = $('p.warning_message');
		if(hasError)
			warningElement.removeClass("hidden");
		else
			warningElement.addClass("hidden");
	};
	return {
		init: init
	};
}();
