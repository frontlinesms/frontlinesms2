{
	validation:{ url:"required", "param-name":"required" },
	updateConfirmationScreen:function() {
		var url = $("input[name=url]").val();
		var httpMethod = $("input[name=httpMethod]:checked").val().toUpperCase();
		var requestParameters = "";
		if($(".web-connection-parameter.disabled").is(":hidden")) { 
			requestParameters = i18n("webConnection.none.label")
		} else {
			$('input[name=param-name]').each(function(index) {
				var values = $('input[name=param-value]').get();
				if($(this).val().length > 0) {
					requestParameters += '<p>' + $(this).val() + ':' + $(values[index]).val() + '</p>';
				}
			});
		}
		$("#url-confirm").html('<p>' + url  + '</p>');
		$("#httpMethod-confirm").html('<p>' + httpMethod  + '</p>');
		$("#requestParameters-confirm").html('<p>' + requestParameters  + '</p>');

	}
}

