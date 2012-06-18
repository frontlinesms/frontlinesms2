function validateMobile(field) {
	checkForNonDigits();
	checkForDuplicates();
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

function checkForNonDigits() {
	if($("#duplicate-error").length == 0) {
		$(".numberField").removeClass('error');
		$(".error-message").remove();
	}
	$('#letter-error').remove();
	
	var field = $(".numberField").filter(function() {
		return this.value.match(/[^\+?\d+]/);
	});
	field.addClass('error');
	field.parent(".basic-info").append("<span id='letter-error' class='error-message'><g:message code='fmessage.number.error' /></span>");
}

function checkForDuplicates() {
	var inputNumber = $("#mobile").val();
	var truncatedNumber = inputNumber;
	
	if($("#letter-error").length == 0) {
		$(".numberField").removeClass('error');
		$(".error-message").remove();
	}
	$('#duplicate-error').remove();
	$.ajax({
		type:'GET',
		data: {number: truncatedNumber, contactId: $("#contactId").val()},
		url: url_root + 'contact/checkForDuplicates',
		success: function(data, textStatus){
			if(data && data != '') {
				$("#mobile").addClass('error');
				$("#mobile").parent(".basic-info").append("<span id='duplicate-error' class='error-message'>" + data + "</span>");
			}
		}
	});
}
$(document).ready(function() {
	$("#mobile").trigger('change');
});