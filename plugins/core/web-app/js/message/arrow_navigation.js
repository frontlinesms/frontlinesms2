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
		var previousRow = selectedRow.prevAll("tr:first");
		if(previousRow.attr('id') !== undefined) {
			selectedRow.removeClass("selected");
			previousRow.addClass("selected");
			if(previousRow.attr('id').indexOf("activity") !=-1) {
				//loadRow(previousRow.attr('id').substring('activity-'.length), previousRow);
			} else {
				if($('input:hidden[name=messageSection]').val() != "trash"){
					loadRow(previousRow.attr('id').substring('message-'.length), previousRow);
					selectedRow.find('.message-select-checkbox').prop("checked", false);
					previousRow.find('.message-select-checkbox').prop("checked", true);
				}
			}
		}
	}else{ $("#main-list tbody tr:first").addClass("selected"); }
}

function showNextRow() {
	if(countSelectedMessages() == 1) {
		var selectedRow = $('tr.selected');
		var nextRow = selectedRow.nextAll("tr:first");
		if(nextRow.attr('id') !== undefined) {
			selectedRow.removeClass("selected");
			nextRow.addClass("selected");
			if(nextRow.attr('id').indexOf("activity") !=-1) {
				//loadRow(nextRow.attr('id').substring('activity-'.length), nextRow);
			} else {
				if($('input:hidden[name=messageSection]').val() != "trash"){
					loadRow(nextRow.attr('id').substring('message-'.length), nextRow);
					selectedRow.find('.message-select-checkbox').prop("checked", false);
					nextRow.find('.message-select-checkbox').prop("checked", true);
				}
			}
		}
	}else{ $("#main-list tbody tr:first").addClass("selected"); }
}

function loadRow(id, row) {
	updateSingleCheckedDetails("message", id, row)
}
function countSelectedMessages() {
    return $('tr.selected').size();
}
