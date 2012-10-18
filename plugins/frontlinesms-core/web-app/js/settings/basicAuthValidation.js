$(function() {
	jQuery.validator.addMethod("password", function(value, element) {
			var isValid = true;
			var passwordField = $("input[name=password]")
			var password = passwordField.val();
			var confirmPassword = $("input[name=confirmPassword]").val();
			if(password.length > 0) {
				isValid = password === confirmPassword
				passwordField.removeClass("error");
				passwordField.addClass("valid");
				passwordField.siblings("label[generated=true]").hide();
			} else {
				isValid = false;
			}
			return isValid;
		}, i18n("basic.authentication.password.mismatch"));

	basicAuthValidation.toggleFields("#enabledAuthentication");
	basicAuthValidation.validator("#basic-auth");
});

var basicAuthValidation = {
	validator: function(form){
			var validator = $(form).validate({
				onsubmit: basicAuthValidation.showErrors(form)
			});
			return validator;
		},
	showErrors: function(form) {
			var isValid = false;
			if($("input[type=text]:not(:disabled), input[type=password]:not(:disabled)").length === 0) {
				return true
			}
			$("input:not(:disabled)").each(function() {
				isValid = isValid && basicAuthValidation.validator(form).element($(this));
			});
			return isValid
		},
	toggleFields: function(selector) {
			if($(selector).is(":checked")) {
				$("input[type=text], input[type=password]").attr("disabled", false);
			} else {
				$("input[type=text], input[type=password]").attr("disabled", "disabled");
				$("input[type=text], input[type=password]").removeClass("error");
				$("label[generated=true]").hide();
			}
		}
}