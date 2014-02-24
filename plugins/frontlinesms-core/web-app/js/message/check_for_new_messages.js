// TODO probably want to rename this something more descriptive such as message_list
check_for_new_messages = (function() {
	var
	flashNewMessages = function(mostRecentOldMessage) {
		$("#main-list tbody tr").each(function() {
			if($(this).find("#message-created-date").val() > mostRecentOldMessage) {
				$(this).addClass("message-added-to-list");
			}
		});
	},
	newMessageParamBuilder = function() {
		return {
			messageSection: $("#messageSection").val(),
			ownerId: $('input:hidden[name=ownerId]').val(),
			starred: $('input:hidden[name=starred]').val(),
			inbound: $('input:hidden[name=inbound]').val(),
			failed: $('input:hidden[name=failed]').val() };
	},
	newMessageResponseHandler = function(data) {
		var type = ($('#messageSection').val() === 'missedCalls' ? 'missedCall' : 'fmessage');
		data = data.new_messages;
		if(!data) { return; }
		currentTotal = parseInt($("#messageTotal").val(), 10);
		newTotal = data;
		if(newTotal > currentTotal) {
			newMessageCount = newTotal - currentTotal;
			notificationContents = "<a id='refreshMessageList'>" + i18n(type + ".new.info", newMessageCount) + "</a>";
			if(!$("#main-list #new-message-notification").html()) {
				$('#main-list tbody tr:first').before('<tr id="new-message-notification"><td colspan="5">' + notificationContents + '</td></tr>');
			} else {
				$("#new-message-notification a").replaceWith(notificationContents);
				$("#new-message-notification a").show();
			}
			$("#refreshMessageList").click(refreshList);
		}
	},
	refreshList = function() {
		var section, ownerId, messageId, sortField, sortOrder, mostRecentOldMessage;
		section = $('input:hidden[name=messageSection]').val();
		ownerId = $('input:hidden[name=ownerId]').val();
		messageId = $('input:hidden[name=messageId]').val();
		sortField = $('input:hidden[name=sortField]').val();
		sortOrder = $('input:hidden[name=sortOrder]').val();
		$("#main-list tbody tr").each(function() {
			mostRecentOldMessage = Math.max(mostRecentOldMessage, $(this).find("#message-created-date").val());
		});

		$.get(url_root + (section === 'missedCalls' ? 'missedCall/' : 'message/') + section, { messageId:messageId, ownerId:ownerId, sort:sortField, order:sortOrder }, function(data) {
			$('#messageTotal').replaceWith($(data).find('#messageTotal'));
			$("#new-message-notification").slideUp(500);
			$("#new-message-notification").remove();
			$('#main-list').replaceWith($(data).find('#main-list'));
			flashNewMessages(mostRecentOldMessage);
		});
	},
	init = function() {
		app_info.listen("new_messages", newMessageParamBuilder, newMessageResponseHandler);
	};

	return { init:init };
}());

$(function() {
	check_for_new_messages.init();
});


