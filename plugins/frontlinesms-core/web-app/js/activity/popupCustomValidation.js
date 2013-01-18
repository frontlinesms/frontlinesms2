function aliasCustomValidation() {
	jQuery.validator.addMethod("keywords", function(value, element) {
		var isValid, allAliases;
		isValid = true;
		allAliases = {};
		$('input:not(:disabled).keywords').each(function() {
			var aliases, currentInput;
			currentInput = $(this);
			aliases = currentInput.val().split(",");
			$.each(aliases, function(index, value) {
				alias = value.trim();
				if(alias.length !== 0) {
					alias = alias.toUpperCase();
					if(allAliases.hasOwnProperty(alias) && 
							element.id === currentInput.attr("id")) {
						isValid = false;
						return;
					}
					allAliases[alias] = true;
				}
			});
			if(!isValid) { return; }
		});
		return isValid;
	}, i18n("poll.keywords.validation.error"));

	jQuery.validator.addMethod("validcommas", function(value, element) {
		if (value.trim().length === 0) { return true; }
		return value.match(/^(\s*,*\s*[\w\-]+\s*,*\s*)(,*\s*[\w\-]+\s*,*\s*)*$/) !== null;
	}, i18n("poll.keywords.validation.error.invalid.keyword"));
}

function genericSortingValidation() {
	jQuery.validator.addMethod("sorting-generic-unique", function(value, element) {
		var isValid, keywords, input, rawKeywords;
		isValid = true;
		keywords = {};
		input = $(element);
		rawKeywords = value.toUpperCase();
		if (rawKeywords.charAt( rawKeywords.length-1 ) === ",") {
			rawKeywords = rawKeywords.slice(0, -1);
		}
		input.removeClass("error");
		$.each(rawKeywords.split(","), function(index, value){
			var keyword = value.trim();
			if(keyword.length > 0) {
				if(keywords.hasOwnProperty(keyword)) {
					//not unique
					input.addClass("error");
					isValid = false;
				} else {
					keywords[keyword] = true;
				}
			}
		});
		return isValid;
	}, i18n("activity.generic.sort.validation.unique.error"));

	jQuery.validator.addMethod("sorting-generic-no-spaces", function(value, element) {
		var isValid, keywords, input, rawKeywords;
		isValid = true;
		keywords = {};
		input = $(element);
		rawKeywords = value.toUpperCase();
		if (rawKeywords.charAt( rawKeywords.length-1 ) === ",") {
			rawKeywords = rawKeywords.slice(0, -1);
		}
		input.removeClass("error");
		$.each(rawKeywords.split(","), function(index, value){
			var keyword = value.trim();
			if(keyword.indexOf(" ") !== -1) {
				// not valid
				input.addClass("error");
				isValid = false;
			} else {
				keywords[keyword] = true;
			}
		});
		return isValid;
	}, i18n("validation.nospaces.error"));
}

function customValidationForGroups(){
	jQuery.validator.addMethod("notnull", function(value, element) {
		if($(element).val() == "null") { return false; } else { return true; }
	}, i18n("validation.group.notnull"));
}