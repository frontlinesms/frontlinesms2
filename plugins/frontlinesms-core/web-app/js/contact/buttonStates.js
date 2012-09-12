function enableSaveAndCancel() {
	// Some of these elements are links, and some of them are
	// <input> tags which are replaced with links.  Handling
	// is slightly different for each.
	var selecters = ["#update-single", "#update-all", ".buttons .cancel"];
	for(i=selecters.length-1; i>=0; --i) {
		var element = $(selecters[i]);
		element.removeAttr("disabled");
		element.removeClass("disabled");
		if(element.hasClass("fsms-button-replaced")) {
			element.next().removeClass("disabled");
		}
	}
}

$(function() {
	$(".buttons .cancel").click(function() {
		window.location = window.location;
	});

	$("div.single-contact").keyup(function(event) {
		enableSaveAndCancel();
	});
	
	$("a.remove-field").click(function(event) {
		enableSaveAndCancel();
	});
});

