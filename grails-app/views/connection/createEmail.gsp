<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="connection" />
		<title>Configure new email connection</title>
	</head>
	<body>
		<g:form action="saveEmail" class="newConnection">
			<div class="input">
				<label for="name"><g:message code="fconnection.name.label" default="Name" /></label>
				<g:textField name="name" value="${fconnectionInstance?.name}" />
			</div>

			<div class="input">
				<label for="type"><g:message code="fconnection.type.label" default="Type" /></label>
				<g:textField name="type" value="${fconnectionInstance?.type}" />
			</div>

			<div class="input">
				<label for="camelAddress"><g:message code="fconnection.camelAddress.label" default="Camel Address" /></label>
				<g:textField name="camelAddress" value="${fconnectionInstance?.camelAddress}" />
			</div>

			<g:submitButton class='create' name="create" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
		</g:form>
	</body>
</html>