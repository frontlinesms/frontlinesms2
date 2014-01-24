<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="popup"/>
	</head>
	<body>
		<g:form name="test-details" action="sendTest" >
			<g:hiddenField name="id" value="${connectionInstance?.id}"/>
			<table id="message-info">
				<tr>
					<td>
						<label for="addresses"><g:message code="connection.createtest.number"/></label>
					</td>
					<td>
						<g:textField name="addresses" id="addresses" value=""/>
					</td>
				</tr>
				<tr>
					<td>
						<label for="messageText"><g:message code="connection.createtest.message.label"/></label>
					</td>
					<td>
						<g:textArea name="messageText" id="messageText" value="${g.message(code:'connection.test.message', args:[connectionInstance.name])}"/>
					</td>
				</tr>
			</table>
		</g:form>
	</body>
</html>
