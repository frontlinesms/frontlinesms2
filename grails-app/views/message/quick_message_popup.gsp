<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<div>
		<li>Enter Message</li>
		<li>Select Recipients</li>
		<li>Confirm</li>
	</div>

	<div id="message-text">
		<label for="message">Enter message</label>
		<g:textArea name="message" rows="5" cols="40"/>
	</div>
	<div id="message-recipient">
			<label for="address">Add phone number</label>
			<g:textField name="address"/>
	</div>
	<div id="confirm-send">
	</div>
</div>