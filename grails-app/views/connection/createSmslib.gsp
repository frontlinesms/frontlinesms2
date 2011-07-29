<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings" />
		<title>Settings > Connections > Configure new phone/modem connection</title>
	</head>
	<body>
		<g:form action="saveSmslib" class="newConnection">
			<div class="input field">
				<label for="name"><g:message code="fconnection.name.label" default="Name" /></label>
				<g:textField name="name" value="${fconnectionInstance?.name}" />
			</div>

			<div class="field">
				<label for="port"><g:message code="fconnection.port.label" default="Port" /></label>
				<g:textField name="port" value="${fconnectionInstance?.port}" />
			</div>

			<div class="field">
				<label for="baud"><g:message code="fconnection.baud.label" default="Baud rate" /></label>
				<g:textField name="baud" value="${fconnectionInstance?.baud}" />
			</div>

			<div class="field">
				<label for="pin"><g:message code="fconnection.pin.label" default="PIN" /></label>
				<g:passwordField name="pin" value="${fconnectionInstance?.pin}" />
			</div>

			<g:submitButton class='create' name="create" value="${message(code: 'default.button.create.label', default: 'Save')}" /></span>
		</g:form>
	</body>
</html>
