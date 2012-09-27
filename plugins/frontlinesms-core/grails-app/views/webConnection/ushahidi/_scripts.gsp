{
	validation:{ url:"required", key:"required" },
	updateConfirmationScreen:function() {
		$("input[name=serviceType]").live("change", function() {
			if($(this).val() == "ushahidi") $("#crowdmap-url-suffix").hide()
			else $("#crowdmap-url-suffix").show()
		});
	}
}
