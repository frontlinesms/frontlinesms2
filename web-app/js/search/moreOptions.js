$(document).ready(function() {
	//FIXME ID of elements type customField and contactString has to be redefine
	$("[id^=custom-field-field]").each(function(){
		if (!$("#"+this.id.replace("custom-field-field-","")+"CustomField").val()){
			$(this).hide()
		}
	})
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
	if (!$("#field-contact-name").is(":visible")) {
		$("#contactString").val("")
	}
	hideOptions();
}

function toggleCustomField(customFieldName) {
	$("#custom-field-field-"+customFieldName).toggle();
	$("#custom-field-link-"+customFieldName).toggle();
	if (!$("#custom-field-field-"+customFieldName).is(":visible")) {
		$("#"+customFieldName+"CustomField").val("")
	}
	hideOptions();
}
