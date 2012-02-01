<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="test-details" action="sendTest" >
	  <g:hiddenField name="id" value="${connectionInstance?.id}"/>
		<div id="message-info">
			<div class="field">
				<label for="addresses">Number</label>
				<g:textField name="addresses" id="addresses" value=""/>
			</div>
			<div class="field">
				<label for="messageText">Message</label>
				<g:textArea name="messageText" id="messageText" value="Congratulations from FrontlineSMS \\o/ you have successfully configured ${connectionInstance.name} to send SMS \\o/"/>
			</div>
		</div>
	</g:form>
</div>