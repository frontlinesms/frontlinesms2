$(function() {
	var validator;
	$("#mobile").trigger('change');
	$("form").on('submit', function(e){
		var isValid=$("form").valid();
		if(!isValid) {
			e.preventDefault();
		}
	});
	validator = $("form").validate({
		onsubmit:false,
		errorPlacement:function(error, element) {
			element.parent().append(error); }
	});
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
	jQuery.validator.addMethod("mobileOrNameRequired", function(value, element) {
		var nameField, mobileField, 
			fields = $(".mobileOrNameRequired");
		if(fields.length !== 0) {
			nameField = $(fields[0]);
			mobileField = $(fields[1]);
			if((nameField.val() === "") && (mobileField.val() === "")) {
				return false;
			}
		}
		return true;
	}, i18n("contact.name.validator.invalid"));
});

