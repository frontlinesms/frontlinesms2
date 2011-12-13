$(function() {
	setInterval(checkForNew, 10000);
	$("#refreshMessageList").live('click', refreshList);
});

function checkForNew() {
	var section = $("#messageSection").val();
	var currentTotal = $("#messageTotal").val();
	var newTotal = $("#messageTotal").val();
	$.getJSON(url_root + 'message/getNewMessageCount', {messageSection: section}, function(data) {
		$.each(data, function(key, val) {
		    newTotal = val;
		  });
		if(newTotal > currentTotal) {
			var newMessageCount = newTotal - currentTotal;
			var notificationContents = "<a id='refreshMessageList'>You have " + newMessageCount + " new messages. Click to view</a>"
			if(!$("#new-message-notification").html()) {
				$('#messages').prepend('<div id="new-message-notification">' + notificationContents + '</div>');
			} else {
				$("#new-message-notification a").replaceWith(notificationContents);
			}
		}
	});
}

function refreshList() {
	alert('do it now');
	var section = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	
	$.get(url_root + 'message/' + section, { messageId: messageId, ownerId: ownerId, viewingArchive: false, checkedMessageList: checkedMessageList}, function(data) {
		alert($(data));
		$("#messages").replaceWith($(data).find("#messages"));
	});
	alert('donme');
}