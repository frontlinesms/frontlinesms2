$(function() {
	var validator;
	$("#mobile").trigger('change');
	validator = $("form").validate({
		onsubmit:false,
		errorPlacement:function(error, element) {
			element.parent().append(error); }
	});
	jQuery.validator.addMethod("phoneNumber", function(value, element) {
		var valid, hasChild;
		valid = true;
		hasChar = $(element).val().match(/[^\+?\d+]/);
		if(hasChar !== null) {
			valid = false;
		}
		return valid;
	}, i18n("fmessage.number.error"));
	jQuery.validator.addMethod("unique", function(value, element) {
		var valid, params;
		valid = true;
		params = {};
		params.contactId = $("input[name=contactId]").val();
		params.mobile = $(element).val();
		$.get(url_root + 'contact/checkForDuplicates', params, function(data) {
			valid = data;
			return valid; 
		});
		return valid;
	}, i18n("contact.exists.warn"));
});

