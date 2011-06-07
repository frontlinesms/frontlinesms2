<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings" />
		<title>Settings > Connections > ${connectionInstance?.name}</title>
	</head>
	<body>
		<g:form name="test-details" action="sendTest" >
			<div id="message-info">
				<div class="field">
					<label for="number"><g:message code="message.number.label" default="Number"/></label>
					<g:textField name="number" id="number" value=""/>
				</div>
				<div class="field">
					<label for="message"><g:message code="message.text.label" default="Message"/></label>
					<g:textArea name="message" id="message" value="Congratulations from FrontlineSMS \\o/ you have successfully configured ${connectionInstance.name} to send SMS \\o/"/>
				</div>
			</div>
			<div class="buttons">
				<g:actionSubmit class="send" action="sendTest" value="${message(code: 'default.button.send.label', default: 'Send')}"/>
				<g:link class="cancel" action="list" default="Cancel">Cancel</g:link>
			</div>
		</g:form>
	</body>
</html>
