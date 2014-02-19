new_message_summary = (function() {
	var
	newMessageSummaryResponseHandler = function(data) {
		data = data.new_message_summary;
		updateSpan($("li[entitytype='inbox'] a"), data.inbox, 'unread_message_count');
		updateSpan($("li[entitytype='pending'] a"), data.pending, 'pending_message_count');
		updateSpan($("li[entitytype='missedCalls'] a"), data.missedCalls, 'unread_message_count');
		$.each(data.activities, function(activityId, unreadMessageCount) {
			updateSpan($("li[entitytype='activity'][entityid='" + activityId + "'] a"), unreadMessageCount, 'unread_message_count');
		});
		$.each(data.folders, function(folderId, unreadMessageCount) {
			updateSpan($("li[entitytype='folder'][entityid='" + folderId + "'] a"), unreadMessageCount, 'unread_message_count');
		});
	},
	updateSpan = function(anchorSelecter, value, spanClass) {
		var existingSpan = $(anchorSelecter).find("span." + spanClass);
		var previousValue = 0;
		if($(existingSpan).length) {
			previousValue = $(existingSpan).text();
			$(existingSpan).text(value);
		}
		else {
			$(anchorSelecter).append("<span class='"+ spanClass + "'>"+value+"</span>");
			existingSpan = $(anchorSelecter).find("span." + spanClass);
		}
		if(value == 0) {
			$(existingSpan).addClass("zero");	
		}
		else {
			$(existingSpan).removeClass("zero");	
			if(value > previousValue && spanClass == 'unread_message_count') {
				$(existingSpan).pulse({
					backgroundColor : '#C03283',
					color           : 'white'
				},
				{
					returnDelay : 300,
					interval    : 400,
					pulses      : 1
				});
			}
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


