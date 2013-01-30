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
			toggleApiTab();
		</g:else>
		
		webconnectionDialog.addValidationRules();

		$("#tabs").bind("tabsshow", function(event, ui) {
			webconnectionDialog.updateConfirmationScreen();
		});
	}

	function setType(type) {
		$.getJSON(url_root + "webconnection/" + type + "/config", function(data) {
			var configTab = $("#webconnection-config");
			var confirmTab = $("#webconnection-confirm");
			configTab.html(data.config);
			confirmTab.html(data.confirm);
			magicwand.init(configTab.find('select[id^="magicwand-select"]'));

			$("#webconnection-confirm").html(data.confirm);
			webconnectionDialog.setScripts(eval("(" + data.scripts + ")"));
			webconnectionDialog.updateConfirmationScreen();
		});
	}
		
</r:script>

