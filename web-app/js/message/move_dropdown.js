$(document).ready(function() {
	$("#message-actions").change(moveAction);
});

function moveAction() {
	var me = $(this).find('option:selected');
	var mesId = $("#message-id").val()
	if(me.hasClass('na')) return;
	if(me.hasClass('poll')) {
		var section = 'poll';
	} else if(me.hasClass('folder')) {
		var section = 'folder';
	}
	alert(mesId);
	$.ajax({
		type:'POST',
		data: {messageSection: section, messageId: mesId, ownerId: me.val()},
		url: '/frontlinesms2/message/move',
		success: function(data) {
			window.location = "/frontlinesms2/message/" + section + "?messageId=" + mesId + "&ownerId=" + me.val();
		}
	});
}