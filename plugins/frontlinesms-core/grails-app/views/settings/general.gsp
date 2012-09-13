<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="settings.general.header"/></title>
		<export:resource/>
	</head>
	<body>
		<div id="body-content-head">
			<h1><g:message code="layout.settings.header"/></h1>
		</div>
		<div id="body-content">
			<div id="language">
				<h2><g:message code="language.label"/></h2>
				<p><g:message code="language.prompt"/></p>
				<g:form action="selectLocale" method="post">
					<g:select class="dropdown" name="language"
							from="${languageList}"
							optionKey="key" optionValue="value"
							noSelection="[currentLanguage:languageList[currentLanguage]?:'English']"
							onchange="\$(this).parent().submit()" />
				</g:form>
				<div class="clearfix"></div>
			</div>
			<div id="import">
				<h2><g:message code="import.label"/></h2>
				<p class="info">
					<g:message code="import.backup.label"/>
				</p>
				<g:uploadForm name="importForm" controller="import" action="importData" method="post">
					<label for="data"><g:message code="import.prompt.type"/></label>
					<div class="radio-choice">
						<input type="radio" name="data" value="contacts" checked="checked"/>
						<g:message code="import.contacts"/>
					</div>
					<div class="radio-choice">
						<input type="radio" name="data" value="messages"/>
						<g:message code="import.messages"/>
					</div>
					<p class="info"><g:message code="import.version1.info"/></p>
					<input type="file" name="importCsvFile" onchange="this.form.submit();"/>
					<label for="importCsvFile"><g:message code="import.prompt"/></label>
				</g:uploadForm>
			</div>
		</div>
	</body>
</html>
