// TODO probably want to rename this something more descriptive such as message_list
new_message_summary = (function() {
	var
	newMessageSummaryResponseHandler = function(data) {
		console.log(" woop! ");
		console.log(data);
	},
	init = function() {
		app_info.listen("new_message_summary", newMessageSummaryResponseHandler);
	};

	return { init:init };
}());

$(function() {
	new_message_summary.init();
});


