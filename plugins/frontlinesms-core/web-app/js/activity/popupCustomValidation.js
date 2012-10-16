function aliasCustomValidation(){
	jQuery.validator.addMethod("keywords", function(value, element) {
		var isValid = true;
		var allAliases = {}
		$('input:not(:disabled).keywords').each(function() {
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
	}, i18n("poll.keywords.validation.error"));

	jQuery.validator.addMethod("validcommas", function(value, element) {
		if (value.trim().length == 0){ return true; }
		return value.match(/^(\s*,*\s*[\w-]+\s*,*\s*)(,*\s*[\w-]+\s*,*\s*)*$/) !== null;
	}, i18n("poll.keywords.validation.error.invalid.keyword"));
}

function genericSortingValidation() {
	jQuery.validator.addMethod("sorting-generic", function(value, element) {
		var isValid = true;
		var keywords = {};
		var input = $(element);
		var rawKeywords = value;
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
	}, i18n("activity.generic.sort.validation.error"));
};