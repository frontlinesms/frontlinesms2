$(function() {
	$('#activityId').change(toggleInArchiveEnabled) 
});

function toggleInArchiveEnabled() {
	if ($(this).val() != "") {
		var checkbox = $('#inArchive');
		checkbox.attr("checked", "true");
		checkbox.attr("disabled", "disabled");
		$('label[for="inArchive"').attr("disabled", "disabled");
	}
	else {
		checkbox.removeAttr("disabled");
		$('label[for="inArchive"').removeAttr("disabled");
	}

}
