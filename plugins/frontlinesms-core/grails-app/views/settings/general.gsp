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
			<div id="basic-authentication">
				<h2><g:message code="basic.authentication" /></h2>
				<p class="info">
					<g:message code="basic.authentication.label"/>
				</p>
				<g:form name="basic-auth" action="basicAuth" controller="settings">
					<table>
						<tbody>
							<tr>
								<td><label for="enabledAuthentication"><g:message code="basic.authentication.enable"/></label></td>
								<td><g:checkBox name="enabledAuthentication" value="true" checked="${enabledAuthentication ? 'true':''}" onclick="basicAuthValidation.toggleFields(this)"/></td>
							</tr>
							<tr>
								<td><label for="username"><g:message code="basic.authentication.username"/></label></td>
								<td><g:textField name="username" class="required" value="${username}"/></td>
							</tr>
							<tr>
								<td><label for="password"><g:message code="basic.authentication.password"/></label></td>
								<td><g:passwordField name="password" class="required" value="${password}" /></td>
							</tr>
							<tr>
								<td><label for="confirmPassword"><g:message code="basic.authentication.confirm.password"/></label></td>
								<td><g:passwordField name="confirmPassword" class="required password" value="" /></td>
							</tr>
							<tr>
								<td></td>
								<td><g:submitButton name="save" class="btn" onclick="basicAuthValidation.showErrors()" value="${message(code:'action.save')}"/></td>
							</tr>
						</tbody>
					</table>
				</g:form>
			</div>
			<div id="database-backup"></div>
		</div>
	</body>
</html>
