var custom_activity = (function() {
	var
	addStep = function(stepName) {
		sanchez.append("#custom-activity-config-container", "step-" + stepName, { stepId:'', groupId:'', autoreplyText:'' });
	},
	removeStep = function() {
		var p = $(this).parent().parent();
		p.fadeOut(300, function() { $(this).remove(); });
	},
	initAddStepButton = function(stepName) {
		$("#add-" + stepName + "-action-step").click(
			function() {
				addStep(stepName);
			});
	},
	init = function() {
		var i, steps = custom_activity.steps;
		for(i=steps.length-1; i>=0; --i) {
			initAddStepButton(steps[i]);
		}
		$('.remove-step').live("click", removeStep);
	};
	return {
		init:init
	};
}());


