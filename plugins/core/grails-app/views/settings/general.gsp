<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="settings.general.header" /></title>
		<export:resource />
	</head>
	<body>
		<div id="general">
			<div id="import">
				<h2><g:message code="import.label" /></h2>
				<p class="description">
					<g:message code="import.backup.label" />
				</p>
				<g:uploadForm name="importForm" controller="import" action="importData" method="post">
					<div>
						<h3 class="importinfo"><g:message code="import.prompt.type" /></h3>
						<input type="radio" name="data" value="contacts" checked="checked" />
						<g:message code="import.contacts" /><br />
						<input type="radio" name="data" value="messages" /><g:message code="import.messages" />
					</div>
					<p class="importinfo"><g:message code="import.version1.info" /></p>
					<input type="file" id="importCsvFile" name="importCsvFile" onchange="this.form.submit();"/>
					<label for="importCsvFile" class="importinfoselect"><g:message code="import.prompt" /></label>
				</g:uploadForm>
			</div>
		</div>
	</body>
</html>
