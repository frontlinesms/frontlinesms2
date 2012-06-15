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
	
	// For use when we chack partial numbers (ie duplicates with vs without country codes)
//	if(inputNumber.length < 7)
//		return;
//	else if(inputNumber.length > 7 && inputNumber.length < 10)
//		truncatedNumber = inputNumber;
//	else
//	if(inputNumber.length > 9)
//		truncatedNumber = inputNumber.substring(inputNumber.length - 9);
	
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

function showWarning(){
	$("div.warning").fadeIn();
}

function hideWarning(){
	$("div.warning").fadeOut();
}