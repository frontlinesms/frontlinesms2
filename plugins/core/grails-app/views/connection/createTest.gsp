<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="test-details" action="sendTest" >
	  <g:hiddenField name="id" value="${connectionInstance?.id}"/>
		<div id="message-info">
			<div class="field">
				<label for="addresses"><g:message code="connection.createtest.number" /></label>
				<g:textField name="addresses" id="addresses" value=""/>
			</div>
			<div class="field">
				<label for="messageText"><g:message code="connection.createtest.message.label" /></label>
																		
				<g:textArea name="messageText" id="messageText" value="${g.message(code:'connection.createtest.message.text', args:[connectionInstance.name])}"/>

			</div>
		</div>
	</g:form>
</div>