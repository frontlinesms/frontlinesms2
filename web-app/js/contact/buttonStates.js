function enableSaveAndCancel() {
	$("#update-single").attr("disabled", false);
	$("#update-all").attr("disabled", false);
	$(".buttons .cancel").attr("disabled", false);
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
