var routing = (function() {
	var
	init = function () {
		$('input[name^="routeRule"]').change(handleChange);
		initializeChangeListeners();
		handleChange();
	},
	handleChange = function() {
		var checkedBoxCount = $('input[name^="routeRule"]:checked').size(),
		totalBoxCount = $('input[name^="routeRule"]').size();
		showOrHideWarning(totalBoxCount > 0 && checkedBoxCount === 0);
	},
	initializeChangeListeners = function() {
		$('input[name^="routeRule"]')
			.change(showUpdateInProgressSpinner)
			.change(updateRoutingRules);
		$("ul.sortable.checklist").sortable({
			update: function(event, ui) {
				updateRoutingRules();
				$(ui.item).find(".progress").addClass("updating");
			}
		});
	},
	showUpdateInProgressSpinner = function() {
		var checkBox = $(this);
		checkBox.parent().find(".progress").addClass("updating");
	},
	updateRoutingRules = function() {
		var routingForm = $("#routing-form"),
			formUrl = routingForm.attr("action"),
			routingUseOrder = [],
			formData = routingForm.serialize();
		routingForm.find("input[type=checkbox]:checked").each(function() {
			routingUseOrder.push($(this).val());
		});
		routingForm.find("input[name=routingUseOrder]").val(routingUseOrder.join());

		$.ajax({
			url : formUrl,
			type : "post",
			data : routingForm.serialize(),
			success : function(data) {
				var progressIndicator = routingForm.find(".progress.updating");
				progressIndicator.removeClass("updating");
				progressIndicator.addClass("fa fa-check");
				progressIndicator.fadeOut(1000, function() { $(this).removeClass("fa fa-check").css("display", ""); });
			}
		});
	},
	refreshRoutingRules = function() {
		$.ajax({
			type: 'POST',
			url: url_root + 'connection/routingRules',
			success: function(data) {
				$('#routing-preferences').html($(data).html());
				init();
			}
		});
	},
	showOrHideWarning = function(hasError) {
		var warningElement = $('p.warning_message');
		if(hasError) {
			warningElement.removeClass("hidden");
		}
		else {
			warningElement.addClass("hidden");
		}
	};
	return {
		init: init,
		refreshRoutingRules: refreshRoutingRules
	};
}());
