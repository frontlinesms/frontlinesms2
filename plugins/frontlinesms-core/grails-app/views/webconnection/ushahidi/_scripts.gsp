{
	validation:{ url:"required", key:"required", keyword:"required"},
	updateConfirmationScreen:function() {
		$("#webconnection-config label.hidden").hide();
		$("input[name=serviceType]").live("change", function() {
			var show = $(this).val();
			var hide = (show==="ushahidi")? "crowdmap": "ushahidi";
			var container = $("#webconnection-config");
			container.find("label." + show).show();
			container.find("label." + hide).hide();
		});
		var keyword = $("input[name=httpMethod]:checked").val() || i18n("webconnection.none.label");
		$("#confirm-service").html('<p style="text-transform:capitalize">' + $("input[name=serviceType]:checked").val() + '</p>');
		$("#confirm-url").html('<p>' + $("input[name=url]").val()  + '</p>');
		$("#confirm-key").html('<p>' + $("input[name=key]").val()  + '</p>');
		$("#confirm-keyword").html('<p>' + keyword  + '</p>');
	}
}
