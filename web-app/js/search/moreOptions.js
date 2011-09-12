$(document).ready(function() {
	$("[id^=custom-field]").hide();
	if(!$("#contactString").val()) {
		$("#field-contact-name").hide();
	}
	$("#expanded-search-options").hide();
});

function expandOptions() {
	$("#expanded-search-options").show();
}

function toggleContactNameField() {
	$("#field-contact-name").toggle();
	$("#expanded-search-options").hide();
}

function toggleCustomField(customFieldName) {
	$("#custom-field-"+customFieldName).toggle();
	$("#expanded-search-options").hide();
}
