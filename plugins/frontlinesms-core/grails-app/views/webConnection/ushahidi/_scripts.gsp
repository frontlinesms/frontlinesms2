{
	validation:{ url:"required", key:"required" },
	updateConfirmationScreen:function() {
		$("#webconnection-config label.hidden").hide();
		$("input[name=serviceType]").live("change", function() {
			var show = $(this).val();
			var hide = (show==="ushahidi")? "crowdmap": "ushahidi";
			var container = $("#webconnection-config");
			container.find("label." + show).show();
			container.find("label." + hide).hide();
		});
	}
}
