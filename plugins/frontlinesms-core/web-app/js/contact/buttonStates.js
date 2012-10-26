function enableSaveAndCancel() {
	var element, selecters;
	// Some of these elements are links, and some of them are
	// <input> tags which are replaced with links.  Handling
	// is slightly different for each.
	selecters = ["#update-single", "#update-all", ".buttons .cancel"];
	for(i=selecters.length-1; i>=0; --i) {
		element = $(selecters[i]);
		element.removeAttr("disabled");
		element.removeClass("disabled");
		if(element.hasClass("fsms-button-replaced")) {
			element.next().removeClass("disabled");
		}
	}
}

$(function() {
	$(".buttons .cancel").click(function() {
		window.location.reload();
	});
	$("div.single-contact").keyup(enableSaveAndCancel);
	$("a.remove-field").click(enableSaveAndCancel);
});

