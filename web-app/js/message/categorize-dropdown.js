$(document).ready(function() {
	$("#categorise_options").hide()
	$("#categorise_dropdown").change(categoriseClickAction)
});

function categoriseClickAction() {
	$("#categorise_options").hide();
	var me = $(this);
	var responseValue = me.val();
	var responseId = responseValue.split("-");
	var id = $("#message-id").val();
	var owner = $("#owner-id").val();
	
	$.ajax({
		type:'POST',
		data: {responseId: responseId[1], messageId: id, ownerId: owner},
		url: url_root + 'message/changeResponse',
		success: function(data) { location.reload(); }
	});
	$("#categorize-dropdown").val("na");
}