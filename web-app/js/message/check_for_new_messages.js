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
			
			if(!$("#new-message-notification").html())
				$('#messages').prepend('<div id="new-message-notification">' + notificationContents + '</div>');
			else
				$("#new-message-notification a").replaceWith(notificationContents);
		}
	});
}

function refreshList() {
	var section = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	var messageId = $('input:hidden[name=messageId]').val();
	var sortField = $('input:hidden[name=sortField]').val();
	var sortOrder = $('input:hidden[name=sortOrder]').val();
	
	var mostRecentOldMessage;
	$("#message-list tbody tr").each(function() {
		if($(this).find("#message-created-date").val() > mostRecentOldMessage || !mostRecentOldMessage)
			mostRecentOldMessage = $(this).find("#message-created-date").val();
	});
	
	$.get(url_root + 'message/' + section, { messageId: messageId, ownerId: ownerId, viewingArchive: false, sort: sortField, order: sortOrder}, function(data) {
		$('#messageTotal').replaceWith($(data).find('#messageTotal'));
		$("#new-message-notification").slideUp(500);
		$('#message-list').replaceWith($(data).find('#message-list'));
		flashNewMessages(mostRecentOldMessage);
	});
}

function flashNewMessages(mostRecentOldMessage) {
	$("#message-list tbody tr").each(function() {
		if($(this).find("#message-created-date").val() > mostRecentOldMessage)
			$(this).addClass("message-added-to-list");
	});
}