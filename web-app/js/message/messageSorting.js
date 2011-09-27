$(document).ready(function() {
	alert($("#sortField").val());
	if($("#sortField").val() == 'dateCreated') {
		$("#timestamp-header").addClass('desc');
	}
});