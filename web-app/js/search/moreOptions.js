$(document).ready(function() {
	$("[id^=custom-field-field]").hide();
	if(!$("#contactString").val()) {
		$("#field-contact-name").hide();
	}
	$("#expanded-search-options").hide();
	$("#more-search-options").live("click",expandOptions)
});

function expandOptions() {
	$("#expanded-search-options").show();
	$("[id^=custom-field-field]:visible").each(function()
	{
		$("#"+this.id.replace("custom-field-field","custom-field-link")).hide();
	})
	$("#more-search-options").die("click").live("click",hideOptions)
}

function hideOptions() {
	$("#expanded-search-options").hide();
	$("#more-search-options").die("click").live("click",expandOptions)
}

function toggleContactNameField() {
	$("#field-contact-name").toggle();
	$("#field-link-contact-name").toggle();
	hideOptions();
}

function toggleCustomField(customFieldName) {
	$("#custom-field-field-"+customFieldName).toggle();
	$("#custom-field-link-"+customFieldName).toggle();
	hideOptions();
}
