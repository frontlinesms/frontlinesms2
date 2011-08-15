<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="test-details" action="sendTest" >
	  <g:hiddenField name="id" value="${connectionInstance?.id}"/>
		<div id="message-info">
			<div class="field">
				<label for="number">Number</label>
				<g:textField name="number" id="number" value=""/>
			</div>
			<div class="field">
				<label for="message">Message</label>
				<g:textArea name="message" id="message" value="Congratulations from FrontlineSMS \\o/ you have successfully configured ${connectionInstance.name} to send SMS \\o/"/>
			</div>
		</div>
	</g:form>
</div>