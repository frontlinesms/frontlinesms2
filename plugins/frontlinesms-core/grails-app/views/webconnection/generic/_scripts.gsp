{
	validation:{ url:"required", "param-name":"required", keyword:"required"},
	updateConfirmationScreen:function() {
		var url = $("input[name=url]").val();
		var httpMethod = $("input[name=httpMethod]:checked").val().toUpperCase();
		var requestParameters = "";
		var keyword = (function(){
			if($("input[name='sorting']:checked").attr('value') == "enabled") {
				return $("input#keywords").val();
			} else {
				return i18n("webconnection.none.label");
			}
		})();
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

		if(mediumPopup.getCurrentTabIndex() === mediumPopup.getTabLength()) {
			webconnectionDialog.showTestRouteBtn();
		}
		
		$("#confirm-url").html('<p>' + url  + '</p>');
		$("#confirm-httpMethod").html('<p>' + httpMethod  + '</p>');
		$("#confirm-keyword").html('<p>' + keyword  + '</p>');
		$("#confirm-key").html('<p>' + apiKey + '</p>');
		$("#confirm-parameters").html('<p>' + requestParameters  + '</p>');

	},
	handlers: (function() {
		var removeRule = function(_removeAnchor) {
			var row = $(_removeAnchor).closest('.web-connection-parameter');
			if(row.find("#param-name.error").is(":visible") && $(".error").size() < 4) { $(".error-panel").hide(); }
			if($('.web-connection-parameter').length === 1) {
				row.addClass("disabled");
				row.find("input").removeClass("error");
				row.find("input").attr("disabled", "disabled");
				row.hide();
			} else { row.remove();}
		}

		var autofillValue = function(list) {
			var varName = $(list).val();
			if(varName !== "na") {
				$(list).parents(".web-connection-parameter").find("input[name=param-value]").val("\$\{" + varName + "\}");
			}
			$(list).trigger("keyup");
		}

		var addNewParam = function(tableParent) {
			if($('.web-connection-parameter:hidden').length === 1) {
					$('.web-connection-parameter').show();
					$('.web-connection-parameter').removeClass("disabled");
					$('.web-connection-parameter').find("input").attr("disabled", false);
					return;
			}
			var template = $('.web-connection-parameter').last();
			var target = "param.value";
			// Selectmenu is destroyed here to allow cloning. Rebuilt after clone
			if($(".error").size() > 1) { $(".error-panel").show(); }
			template.find("select").selectmenu("destroy");
			template.find('.remove-command').show();
			var newRow = template.clone();
			newRow.removeAttr("id");
			newRow.find('input.param-name').val("");
			newRow.find('input.param-value').val("");
			newRow.find('.remove-command').show();
			$(tableParent).find("tbody").append(newRow);
			magicwand.init(newRow.find('select[id^="magicwand-select"]'));
			magicwand.reset(template.find("select"));
		}
		return {
			removeRule:removeRule,
			autofillValue:autofillValue,
			addNewParam:addNewParam
		}
	})()
}

