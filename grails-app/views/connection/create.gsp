<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head><title>Simple GSP page</title></head>
	<body>
		<ul id="connectionTypes">
			<g:link action="createSmslib" class="smslib">Phone/Modem</g:link>
			<g:link action="createEmail" class="email">Email</g:link>
		</ul>
	</body>
</html>