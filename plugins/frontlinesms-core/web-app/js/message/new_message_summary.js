new_message_summary = (function() {
	var
	newMessageSummaryResponseHandler = function(data) {
		data = data.new_message_summary;
		console.log("New message summary f0llows");
		console.log(data);
		updateSpan($("li[entitytype='inbox']"), data.inbox);
		$.each(data.activities, function(activityId, unreadMessageCount) {
			updateSpan($("li[entitytype='activity'][entityid='" + activityId + "']"), unreadMessageCount);
		});
		$.each(data.folders, function(folderId, unreadMessageCount) {
			updateSpan($("li[entitytype='folder'][entityid='" + folderId + "']"), unreadMessageCount);
		});
	},
	updateSpan = function(liSelector, value) {
		var existingSpan = $(liSelector).find("span.unread_message_count");
		if($(existingSpan).length) {
			$(existingSpan).text(value);
			// TODO flash if number is greater than previous value
		}
		else {
			$(liSelector).append("<span class='unread_message_count'>"+value+"</span>");
		}
	},
	init = function() {
		app_info.listen("new_message_summary", newMessageSummaryResponseHandler);
	};

	return { init:init };
}());

$(function() {
	new_message_summary.init();
});


