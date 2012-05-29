$(function() {
	setInterval(checkForNew, 10000);
	$("#refreshMessageList").live('click', refreshList);
});

function checkForNew() {
	var params = {};
	params.messageSection = $("#messageSection").val();
	params.ownerId = $('input:hidden[name=ownerId]').val();
	params.starred = $('input:hidden[name=starred]').val();
	params.failed = $('input:hidden[name=failed]').val();


	var currentTotal = $("#messageTotal").val();
	var newTotal = $("#messageTotal").val();
	
	$.getJSON(url_root + 'message/newMessageCount', params, function(data) {
		if(data == null) return;
		$.each(data, function(key, val) {
		    newTotal = val;
		});

		if(newTotal > currentTotal) {
			var newMessageCount = newTotal - currentTotal;
			var notificationContents = "<a id='refreshMessageList'>" + i18n("fmessage.new.info", newMessageCount) + "</a>"
			if(!$("#message-list #new-message-notification").html())
				$('#message-list .main-table #message-sorter').after('<tr id="new-message-notification"><td /><td /><td /><td><div>' + notificationContents + '</div></td><td /></tr>');
			else {
				$("#new-message-notification a").replaceWith(notificationContents);
				$("#new-message-notification a").show();
			}
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
	
	$.get(url_root + 'message/' + section, { messageId: messageId, ownerId: ownerId, sort: sortField, order: sortOrder}, function(data) {
		$('#messageTotal').replaceWith($(data).find('#messageTotal'));
		$("#new-message-notification").slideUp(500);
		$("#new-message-notification").remove();
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
