<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources />
		<g:layoutHead />
		<g:javascript src="application.js"/>
		<g:javascript src="popup.js"/>
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
	</head>
	<body>
		<g:render template="/tabs"/>
		<g:render template="/message/quick_message"/>
		<g:render template="/flash"/>
		<div id="main">
			<g:render template="menu"/>
			<g:render template="contact_list"/>
			<g:layoutBody />
		</div>
	</body>
</html>
