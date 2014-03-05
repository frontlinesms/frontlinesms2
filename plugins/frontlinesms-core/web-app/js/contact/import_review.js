var contactImportReviewer;
$(function() {
	contactImportReviewer = new ContactImportReviewer();
});

var ContactImportReviewer = function() {
	var
	getCellValue = function(x, y) {
		return JSON.stringify($('input[data-x=' + x + '][data-y=' + y + ']').val());
	},
	checkForRecognisedHeader = function() {
		var element, val;
		element = $(this);
		element.removeClass();
		val = element.val();
		if($.inArray(val, $('input[name=recognisedTitles]').val().split(',')) > -1) {
			element.addClass('recognised');
		} else if(val) {
			element.addClass('unrecognised');
		} else {
			element.addClass('empty');
		}
	},
	submit = function() {
		$('input[name=csv]').val(generateCSV());
		$('form[name=reviewForm]').submit();
	},
	generateCSV = function() {
		var rowCount, columnCount, currentLine, result = [];
		rowCount = $(".import-review tr").size();
		columnCount = $(".import-review tr:first td").size();
		for(y = 0; y < rowCount; y++) {
			currentLine = [];
			for(x = 0; x < columnCount; x++) {
				currentLine.push(getCellValue(x, y));
			}
			result.push(currentLine.join(','));
		}
		return result.join('\n');
	},
	init = function() {
		var inputCells = $('tr.headers td input');
		inputCells.keyup(checkForRecognisedHeader);
		inputCells.keyup();
	};
	this.init = init;
	this.submit = submit;
};

