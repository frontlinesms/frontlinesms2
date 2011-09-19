$(document.documentElement).keyup(function (event) {
	var key = 0;
	
	if (event == null) {
		key = event.keyCode;
	} else { // mozilla
		key = event.which;
	}
	
	if(key === 38) {
		showPreviousMessage();
		event.stopPropagation();
		return false;
	}
	
	if(key === 40) {
		showNextMessage();
		event.stopPropagation();
		return false;
	}
});

function showPreviousMessage() {
	if(countSelectedMessages() == 1) {
		var selectedMessage = $('tr.selected');
		var previousMessage = selectedMessage.prev()
		if(previousMessage.attr('id') !== undefined) {
			loadMessage(previousMessage.attr('id').substring('message-'.length));
		}
	}
}

function showNextMessage() {
	if(countSelectedMessages() == 1) {
		var selectedMessage = $('tr.selected');
		var nextMessage = selectedMessage.next()
		if(nextMessage.attr('id') !== undefined) {
			loadMessage(nextMessage.attr('id').substring('message-'.length));
		}
	}
}

function loadMessage(id) {
	var url = $(".displayName-" + id).attr("href");
	$.get(url, function(data) {
		$('#message-list').replaceWith($(data).find('#message-list'));
		$('#message-details #message-id').replaceWith($(data).find('#message-details #message-id'));
		$('#message-details #message-src').replaceWith($(data).find('#message-details #message-src'));
		$('#message-details #single-message #message-info').replaceWith($(data).find('#message-details #single-message #message-info'));
		$('#message-details #single-message #other_btns').replaceWith($(data).find('#message-details #single-message #other_btns'));
		$('#message-details #single-message #poll-actions').replaceWith($(data).find('#message-details #single-message #poll-actions'));
	});
}
function countSelectedMessages() {
    return $('tr.selected').size();
}
