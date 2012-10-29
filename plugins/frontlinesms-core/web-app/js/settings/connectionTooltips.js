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

var connectionTooltips = {
	init: function(connectionType){
			$(".connection-tip").remove();
			if(i18n(connectionType+"fconnection.global.info") != connectionType+"fconnection.global.info")
				$(".fconnection-details").find("table").before("<div span class='info connection-tip'>"+i18n(connectionType+"fconnection.global.info")+"</div>");
			$(".fconnection-details input").each(function(i,e){
				var fieldname = $(this).attr('field');
				var msgcode = connectionType+"fconnection.field."+fieldname+".info";
				if (i18n(msgcode) != msgcode) {
					$(this).closest("tr").children("td:first").append("<p class='connection-field-info connection-tip'>"+i18n(msgcode)+"</p>")
				}
			});
		}
}