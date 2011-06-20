<%@ page contentType="text/html;charset=UTF-8" %>
<div>
<div id="tabs">
	<ul>
		<li><a href="#tabs-1">Enter Message</a></li>
		<li><a href="#tabs-2">Select Recipients</a></li>
		<li><a href="#tabs-3">Confirm</a></li>
	</ul>
	<div id="tabs-1">
		<label for="message">Enter message</label>
		<g:textArea name="message" rows="5" cols="40"/>
	</div>
	<div id="tabs-2">
		<label for="address">Add phone number</label>
		<g:textField name="address"/>
	</div>
	<div id="tabs-3">
		<label>Do you want to send?</label>
	</div>
</div>
</div>
