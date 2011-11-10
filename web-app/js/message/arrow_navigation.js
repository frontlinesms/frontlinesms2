$(document.documentElement).keyup(function (event) {
	var key = 0;
	
	if (event == null) {
		key = event.keyCode;
	} else { // mozilla
		key = event.which;
	}
	
	if(key === 38) {
		showPreviousRow();
		event.stopPropagation();
		return false;
	}
	
	if(key === 40) {
		showNextRow();
		event.stopPropagation();
		return false;
	}
});

function showPreviousRow() {
	if(countSelectedMessages() == 1) {
		var selectedRow = $('tr.selected');
		var previousRow = selectedRow.prev()
		if(previousRow.attr('id') !== undefined) {
			if(previousRow.attr('id').indexOf("activity") !=-1) {
				loadRow(previousRow.attr('id').substring('activity-'.length));
			} else {
				loadRow(previousRow.attr('id').substring('message-'.length));
			}
		}
	}
}

function showNextRow() {
	if(countSelectedMessages() == 1) {
		var selectedRow = $('tr.selected');
		var nextRow = selectedRow.next()
		if(nextRow.attr('id') !== undefined) {
			if(nextRow.attr('id').indexOf("activity") !=-1) {
				loadRow(nextRow.attr('id').substring('activity-'.length));
			} else {
				loadRow(nextRow.attr('id').substring('message-'.length));
			}
		}
	}
}

function loadRow(id) {
	var url = $(".displayName-" + id).attr("href");
	$.get(url, function(data) {
		$('#message-list').replaceWith($(data).find('#message-list'));
		$('#message-details').replaceWith($(data).find('#message-details'));
	});
}
function countSelectedMessages() {
    return $('tr.selected').size();
}
