<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="settings.general.header"/></title>
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
				<g:uploadForm name="importForm" controller="import" action="importData" method="post">
					<fsms:radioGroup name="data" title="import.prompt.type"
							values="contacts,messages"
							labelPrefix="import."
							checked="contacts"/>
					<fsms:info message="import.version1.info"/>
					<input type="file" name="importCsvFile" onchange="this.form.submit();"/>
					<label for="importCsvFile"><g:message code="import.prompt"/></label>
				</g:uploadForm>
				<g:if test="${failedContacts}">
					<div id="failed-contacts">
						<h2><g:message code="import.contact.failed.label"/></h2>
						<p class='info'><g:message code="import.contact.failed.info" args="[numberOfFailedLines]"/></p>
						<g:form controller="import" action="failedContacts">
							<g:hiddenField name='failedContacts' value='${failedContacts.trim()}'/>
							<g:submitButton name="failedContactSubmit" value="${message(code:'import.contact.failed.download')}" class="btn"/>
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
	
