{
	validation:{ displayed_url:"required", key:"required", keyword:"required"},
	updateConfirmationScreen:function() {
		var fsmsApiKey = ('('+ i18n('webconnection.api.disabled') +')');
		if ($("input[name=enableApi]").is(":checked"))
			fsmsApiKey = $("input[name=secret]").val();
		$("#webconnection-config label.hidden").hide();
		var selectType = function(ctx) {
			var show = $(ctx).val();
			var hide = (show==="ushahidi")? "crowdmap": "ushahidi";
			var container = $("#webconnection-config");
			container.find("label." + show).show();
			container.find("label." + hide).hide();
		};
		$("input[name=serviceType]").live("change", function() {
			selectType(this);
		});

		selectType($("input[name=serviceType]:checked"));

		$("input[name=displayed_url]").live("change", function(){
			var isCrowdMap = $("input[name=serviceType]:checked").val() === "crowdmap"
			if(isCrowdMap) $("input[name=url]").val("https://" + $(this).val() + ".crowdmap.com" + "/frontlinesms/");
			else $("input[name=url]").val($(this).val());
		});
		var keyword = $("input[name=keywords]").val() || i18n("webconnection.none.label");
		$("#confirm-service").html('<p style="text-transform:capitalize">' + $("input[name=serviceType]:checked").val() + '</p>');
		$("#confirm-url").html('<p>' + $("input[name=url]").val()  + '</p>');
		$("#confirm-fsmskey").html('<p>' + fsmsApiKey + '</p>');
		$("#confirm-crowdmapkey").html('<p>' + $('input#key').val() + '</p>');
		$("#confirm-keyword").html('<p>' + keyword  + '</p>');
	},
	handlers: {}
}
