<r:script>
	function initializePopup() {
		custom_activity.steps = ["join", "leave", "reply", "webconnection"];
		custom_activity.init();
		customActivityDialog.addValidationRules();
		
		var initialScripts = <fsms:render template="/webconnection/generic/scripts"/>;
		webconnectionDialog.setScripts(initialScripts);
	}
</r:script>

