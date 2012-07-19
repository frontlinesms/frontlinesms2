function validateMobile(field) {
	var internationFormatWarning = $(field).parent().find(".warning");
	if(field.value=="" || isInternationalFormat(field.value)) {
		internationFormatWarning.hide('fast');
	} else {
		internationFormatWarning.show('fast');
	}
}

function isInternationalFormat(phoneNumber) {
	return phoneNumber.match(/\+\d+/);
}

$(document).ready(function() {
	$("#mobile").trigger('change');
	var validator = $("form").validate();
	jQuery.validator.addMethod("phoneNumber", function(value, element) {
		var valid = true;
		var hasChar = $(element).val().match(/[^\+?\d+]/);
		if(hasChar != null) {
			valid = false;
		}
		return valid;
	}, i18n("fmessage.number.error"));
	jQuery.validator.addMethod("unique", function(value, element) {
		var valid = true;
		var params = {};
		params.contactId = $("input[name=contactId]").val();
		params.mobile = $(element).val();
		$.get(url_root + 'contact/checkForDuplicates', params, function(data){
			valid = data;
			return valid; 
		});
		return valid;
	}, i18n("contact.exists.warn"));

});
