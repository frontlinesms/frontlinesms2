// TODO probably want to rename this something more descriptive such as message_list
new_message_summary = (function() {
	var
	newMessageSummaryResponseHandler = function(data) {
		data = data.new_message_summary
		console.log(data.inbox);
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
			console.log("FOUND, UPDATING");
			$(existingSpan).text(value);
		}
		else {
			console.log("NOT FOUND, CREATING");
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


