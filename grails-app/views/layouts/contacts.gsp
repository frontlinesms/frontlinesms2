<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources />
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<g:javascript src="application.js"/>
		<g:layoutHead />
	</head>
	<body>
		<g:render template="/tabs"/>
		<g:render template="/flash"/>
		<div id="main">
			<g:render template="menu"/>
			<g:render template="contact_list"/>
			<g:layoutBody />
		</div>
	</body>
</html>
