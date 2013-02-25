<%@ page import="frontlinesms2.Webconnection" %>
<r:script>
	
	function initializePopup() {
		<g:if test="${activityInstanceToEdit?.id}">
			var initialScripts = <fsms:render template="/webconnection/${activityInstanceToEdit?.class?.type}/scripts"/>;
			webconnectionDialog.setScripts(initialScripts);
			webconnectionDialog.updateConfirmationScreen()
		</g:if>
		<g:else>
			var initialScripts = <fsms:render template="/webconnection/${Webconnection.implementations[1].type}/scripts"/>;
			webconnectionDialog.setScripts(initialScripts);
			webconnectionDialog.toggleApiTab();
		</g:else>
		
		webconnectionDialog.addValidationRules();

		$("#tabs").bind("tabsshow", function(event, ui) {
			webconnectionDialog.updateConfirmationScreen();
		});
	}
		
</r:script>

