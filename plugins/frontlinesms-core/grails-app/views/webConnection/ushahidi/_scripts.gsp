{
	validation:{ url:"required", key:"required" },
	updateConfirmationScreen:function() {
		$("input[name=serviceType]").live("change", function() {
			if($(this).val() == "ushahidi") $("#crowdmap-url-suffix").hide()
			else $("#crowdmap-url-suffix").show()
		});

		var keyword = $("input[name=httpMethod]:checked").val() || i18n("webConnection.none.label");
		$("#confirm-service").html('<p style="text-transform:capitalize">' + $("input[name=serviceType]:checked").val() + '</p>');
		$("#confirm-url").html('<p>' + $("input[name=url]").val()  + '</p>');
		$("#confirm-key").html('<p>' + $("input[name=key]").val()  + '</p>');
		$("#confirm-keyword").html('<p>' + keyword  + '</p>');
	}
}
