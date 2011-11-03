function checkForNonDigits() {
	$(".numberField").removeClass('error');
	$(".numberField").parent(".basic-info").find('span').remove();
	
	var field = $(".numberField").filter(function() {
        return this.value.match(/[a-zA-Z]/);
    });
	field.addClass('error');
	field.parent(".basic-info").append("<span>You have added a letter to this field, upon saving all letters will be removed.</span>");
}