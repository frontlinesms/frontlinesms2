{
	validation:{ url:"required", "param-name":"required" },
	updateConfirmationScreen:function() {
		var url = $("input[name=url]").val();
		var httpMethod = $("input[name=httpMethod]:checked").val().toUpperCase();
		var requestParameters = "";
		var keyword = $("input[name=keyword]").val() || i18n("webconnection.none.label");
		if($(".web-connection-parameter.disabled").is(":hidden")) { 
			requestParameters = i18n("webconnection.none.label")
		} else {
			$('input[name=param-name]').each(function(index) {
				var values = $('input[name=param-value]').get();
				if($(this).val().length > 0) {
					requestParameters += '<p>' + $(this).val() + ':' + $(values[index]).val() + '</p>';
				}
			});
		}
		$("#confirm-url").html('<p>' + url  + '</p>');
		$("#confirm-httpMethod").html('<p>' + httpMethod  + '</p>');
		$("#confirm-keyword").html('<p>' + keyword  + '</p>');
		$("#confirm-parameters").html('<p>' + requestParameters  + '</p>');

	}
}

