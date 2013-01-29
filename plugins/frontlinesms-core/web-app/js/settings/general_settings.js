
$(function(){
	$("ul.sortable.checklist").sortable();
	$("#routing-form").submit(function() {
		var form = $(this),
		routingUseOrder = [];
		form.find("input[type=checkbox]:checked").each(function() {
			routingUseOrder.push($(this).val());
		});
		form.find("input[name=routingUseOrder]").val(routingUseOrder.join());
		return true;
	});
});

var basicAuthValidation = {
	enable: function() {
			var validatePassword = function(value, element) {
				var confirmPassword, password, passwordField, isValid;
				isValid = true;
				passwordField = $("input[name=password]");
				password = passwordField.val();
				confirmPassword = $("input[name=confirmPassword]").val();
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
			if($("input[type=text]:not(:disabled), input[type=password]:not(:disabled)").length === 0) {
				return true;
			}
			$("input:not(:disabled)").each(function() {
				isValid = isValid && basicAuthValidation.validator(form).element($(this));
			});
			return isValid;
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
};

