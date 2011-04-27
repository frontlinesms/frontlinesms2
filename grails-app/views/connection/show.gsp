<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
	  	<meta name="layout" content="connection" />
		<title>Settings > Connections</title>
	</head>
	<body>
		<g:hiddenField name="id" value="${connectionInstance?.id}"/>
		<g:hiddenField name="version" value="${connectionInstance?.version}"/>
		<div id="connection">
			<div id="connectionname">
				<label for="name"><g:message code="connection.name.label" default="Name"/></label>
				<g:textField name="name" id="name" value="${connectionInstance?.name}"/>
			</div>
			<div id="connectionaddress">
				<label for="address"><g:message code="connection.camelAddress.label" default="camelAddress"/></label>
				<g:textField name="address" id="address" value="${connectionInstance?.camelAddress}"/>
			</div>
		</div>
	</body>
</html>