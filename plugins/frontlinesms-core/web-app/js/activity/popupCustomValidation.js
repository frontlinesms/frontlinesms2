function aliasCustomValidation(){
	jQuery.validator.addMethod("aliases", function(value, element) {
		var isValid = true;
		var allAliases = {}
		$('input:not(:disabled).aliases').each(function() {
			var currentInput = $(this);
			var aliases = currentInput.val().split(",");
			$.each(aliases, function(index, value) {
				alias = value.trim();
				if(alias.length != 0){
					alias = alias.toUpperCase();
					if((alias in allAliases) && (element.id == currentInput.attr("id"))) {
						isValid = false; return;
					}
					else {
						allAliases[alias] = true;
					}
				}
			});
			if(!isValid) { return; }
		});
		return isValid;
	}, i18n("poll.alias.validation.error"));

	jQuery.validator.addMethod("validcommas", function(value, element) {
		if (value.trim().length == 0){ return true; }
		return value.match(/^(\s*,*\s*[\w-]+\s*,*\s*)(,*\s*[\w-]+\s*,*\s*)*$/) !== null;
	}, i18n("poll.alias.validation.error.invalid.alias"));
}

function genericSortingValidation() {
	var isValid = true;
	var keywords = {};
	var input = $("input#keywords");
	var rawKeywords = input.val();
	if (rawKeywords.charAt( rawKeywords.length-1 ) == ",")
		rawKeywords = rawKeywords.slice(0, -1);
	input.removeClass("error");
	$.each(rawKeywords.split(","), function(index, value){
		var keyword = value.trim();
		if(keyword.indexOf(" ") != -1){
			// not valid
			input.addClass("error");
			isValid = false;
		}
		else if(keyword in keywords) {
			//not unique
			input.addClass("error");
			isValid = false;
		}
		else {
			keywords[keyword] = true;
		}
	});
	return isValid;
};