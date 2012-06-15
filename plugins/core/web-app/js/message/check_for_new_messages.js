$(function() {
	setInterval(checkForNew, 10000);
	$("#refreshMessageList").live('click', refreshList);
});

function checkForNew() {
	var params = {
			messageSection: $("#messageSection").val(),
			ownerId: $('input:hidden[name=ownerId]').val(),
			starred: $('input:hidden[name=starred]').val(),
			failed: $('input:hidden[name=failed]').val() };
	
	$.getJSON(url_root + 'message/newMessageCount', params, function(data) {
		if(data == null) return;
		var currentTotal = parseInt($("#messageTotal").val());
		var newTotal = data;

		if(newTotal > currentTotal) {
			var newMessageCount = newTotal - currentTotal;
			var notificationContents = "<a id='refreshMessageList'>" + i18n("fmessage.new.info", newMessageCount) + "</a>"
			if(!$("#main-list #new-message-notification").html())
				$('#main-list tbody tr:first').before('<tr id="new-message-notification"><td colspan="5">' + notificationContents + '</td></tr>');
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
	$("#main-list tbody tr").each(function() {
		if($(this).find("#message-created-date").val() > mostRecentOldMessage || !mostRecentOldMessage)
			mostRecentOldMessage = $(this).find("#message-created-date").val();
	});
	
	$.get(url_root + 'message/' + section, { messageId: messageId, ownerId: ownerId, sort: sortField, order: sortOrder}, function(data) {
		$('#messageTotal').replaceWith($(data).find('#messageTotal'));
		$("#new-message-notification").slideUp(500);
		$("#new-message-notification").remove();
		$('#main-list').replaceWith($(data).find('#main-list'));
		flashNewMessages(mostRecentOldMessage);
	});
}

function flashNewMessages(mostRecentOldMessage) {
	$("#main-list tbody tr").each(function() {
		if($(this).find("#message-created-date").val() > mostRecentOldMessage)
			$(this).addClass("message-added-to-list");
	});
}
