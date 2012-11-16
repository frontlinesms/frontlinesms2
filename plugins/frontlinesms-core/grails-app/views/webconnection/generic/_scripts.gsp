{
	validation:{ url:"required", "param-name":"required", keyword:"required"},
	updateConfirmationScreen:function() {
		var url = $("input[name=url]").val();
		var httpMethod = $("input[name=httpMethod]:checked").val().toUpperCase();
		var requestParameters = "";
		var keyword = $("input#keywords").val() || i18n("webconnection.none.label");
		var apiKey = ('('+ i18n('webconnection.api.disabled') +')');
		if ($("input[name=enableApi]").is(":checked"))
			apiKey = $("input[name=secret]").val();
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
		$("#confirm-key").html('<p>' + apiKey + '</p>');
		$("#confirm-parameters").html('<p>' + requestParameters  + '</p>');

	}
}

