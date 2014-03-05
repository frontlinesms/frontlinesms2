<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="settings.porting.header"/></title>
		<export:resource/>
	</head>
	<body>
		<div id="body-content-head">
			<h1><g:message code="settings.porting"/></h1>
		</div>
		<div id="body-content">
			<div id="import">
				<h2><g:message code="import.label"/></h2>
				<fsms:info message="import.backup.label"/>
				<fsms:render template="/contact/import_contacts" />
				<g:if test="${failedContacts}">
					<div id="failed-contacts">
						<g:form controller="import" action="failedContacts">
							<p class='warning_message'><g:message code="import.contact.failed.info" args="[numberOfFailedLines]"/></p>
							<g:hiddenField name='failedContacts' value='${failedContacts.trim()}'/>
							<g:hiddenField name='format' value='${failedContactsFormat}'/>
							<g:submitButton name="failedContactSubmit" value="${message(code:'import.download.failed.contacts')}" class="btn"/>
						</g:form>
					</div>
				</g:if>
			</div>
			<div id="export">
				<h2><g:message code="export.label"/></h2>
				<fsms:info message="export.backup.label"/>
				<g:form controller="settings" action="porting">
					<fsms:radioGroup name="exportData" title="export.prompt.type" values="allcontacts, inboxmessages" labelPrefix="export." checked="allcontacts" />
					<g:submitButton name="exportSubmit" value="${message(code:'export.submit.label')}" class="btn"/>
				</g:form>	
			</div>
		</div>
	</body>
</html>
<r:script>
	$("#exportSubmit").click(function(e){
		var exportData = $("input[name=exportData]:checked"),
			ajaxData, urlTarget;
		switch(exportData.val()) {
			case "allcontacts":
				urlTarget = "contactWizard";
				popupTitle = "smallpopup.contact.export.title";
				break;
			case "inboxmessages":
				urlTarget = "messageWizard";
				ajaxData = { messageSection:"inbox" };
				popupTitle = "smallpopup.fmessage.export.title";
				break;
		}
		if(urlTarget) {
			$.ajax({
				type: "POST",
				url: url_root + "export/" + urlTarget,
				beforeSend : function() { showThinking(); },
				data: ajaxData,
				success: function(data){
					hideThinking();
					launchSmallPopup(i18n(popupTitle), data, i18n("action.export"));
				}
			});
		}
		e.preventDefault();
	});
</r:script>
	
