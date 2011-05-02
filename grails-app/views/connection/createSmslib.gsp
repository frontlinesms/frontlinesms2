<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="connection" />
		<title>Configure new phone/modem connection</title>
	</head>
	<body>
		<g:form action="saveSmslib" class="newConnection">
			<div class="input">
				<label for="name"><g:message code="fconnection.name.label" default="Name" /></label>
				<g:textField name="name" value="${fconnectionInstance?.name}" />
			</div>

			<div class="input">
				<label for="port"><g:message code="fconnection.port.label" default="Port" /></label>
				<g:textField name="port" value="${fconnectionInstance?.port}" />
			</div>

			<div class="baud">
				<label for="baud"><g:message code="fconnection.camelAddress.label" default="Baud rate" /></label>
				<g:textField name="baud" value="${fconnectionInstance?.baud}" />
			</div>

			<g:submitButton class='create' name="create" value="${message(code: 'default.button.create.label', default: 'Save')}" /></span>
		</g:form>
	</body>
</html>
