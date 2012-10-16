function toggleInArchiveEnabled() {
	var checkbox = $('#inArchive');
	if ($(this).val() !== "") {
		checkbox.attr("checked", "true");
		checkbox.attr("disabled", "disabled");
		$('label[for="inArchive"').attr("disabled", "disabled");
	} else {
		checkbox.removeAttr("disabled");
		$('label[for="inArchive"').removeAttr("disabled");
	}
}

$(function() {
	$('#activityId').change(toggleInArchiveEnabled);
});

