var basicAuthValidation = {
	find: function(selecter) { return $("#basic-auth").find(selecter); },
	enable: function() {
			var validatePassword = function(value, element) {
				var confirmPassword, password, passwordField, isValid;
				isValid = true;
				passwordField = basicAuthValidation.find("input[name=password]");
				password = passwordField.val();
				confirmPassword = basicAuthValidation.find("input[name=confirmPassword]").val();
				if(password.length > 0) {
					isValid = password === confirmPassword;
					passwordField.removeClass("error");
					passwordField.addClass("valid");
					passwordField.siblings("label[generated=true]").hide();
				} else {
					isValid = false;
				}
				return isValid;
			};
			jQuery.validator.addMethod("password", validatePassword, i18n("auth.basic.password.mismatch"));
			basicAuthValidation.toggleFields("#basic-authentication input[name=enabled]");
			basicAuthValidation.validator("#basic-auth");
		},
	validator: function(form){
			var validator = $(form).validate({
				onsubmit: basicAuthValidation.showErrors(form)
			});
			return validator;
		},
	showErrors: function(form) {
			var isValid = false;
			if(basicAuthValidation.find("input[type=text]:not(:disabled), input[type=password]:not(:disabled)").length === 0) {
				return true;
			}
			basicAuthValidation.find("input:not(:disabled)").each(function() {
				isValid = isValid && basicAuthValidation.validator(form).element($(this));
			});
			return isValid;
		},
	toggleFields: function(selector) {
			var inputFields = basicAuthValidation.find("input[type=text], input[type=password]");
			if($(selector).is(":checked")) {
				inputFields.attr("disabled", false);
			} else {
				inputFields.attr("disabled", "disabled");
				inputFields.removeClass("error");
				basicAuthValidation.find("label[generated=true]").hide();
			}
		}
};

