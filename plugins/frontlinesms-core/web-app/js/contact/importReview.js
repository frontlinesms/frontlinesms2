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

	},
	submit = function() {
		$('input[name=csv]').val(generateCSV());
		$('form[name=reviewForm]').submit();
	},
	generateCSV = function() {
		var rowCount, columnCount, currentLine, result = [];
		rowCount = $("#contactImportReview tr").size();
		columnCount = $("#contactImportReview tr:first td").size();
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
		console.log("INIT");
	};
	this.init = init;
	this.submit = submit;
};

