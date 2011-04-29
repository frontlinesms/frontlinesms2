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
				<label for="protocol"><g:message code="fconnection.protocol.label" default="Protocol" /></label>
				<g:select from="${frontlinesms2.EmailProtocol.values()}"
					    value="${fconnectionInstance?.protocol}"
					    name="protocol"
					    noSelection="${['null': '- Select -']}"/>
			</div>
			
			<div class="input">
				<label for="serverName"><g:message code="fconnection.serverName.label" default="Server name" /></label>
				<g:textField name="serverName" value="${fconnectionInstance?.serverName}" />
			</div>

			<div class="input">
				<label for="serverPort"><g:message code="fconnection.serverPort.label" default="Server port" /></label>
				<g:textField name="serverPort" value="${fconnectionInstance?.serverPort}" />
			</div>
			
			<div class="input">
				<label for="username"><g:message code="fconnection.username.label" default="Username" /></label>
				<g:textField name="username" value="${fconnectionInstance?.username}" />
			</div>
			
			<div class="input">
				<label for="password"><g:message code="fconnection.password.label" default="Password" /></label>
				<g:textField name="password" value="${fconnectionInstance?.password}" />
			</div>

			<g:submitButton class='create' name="create" value="${message(code: 'default.button.create.label', default: 'Save')}" /></span>
		</g:form>
	</body>
</html>

