<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:layoutTitle default="Archive"/></title>
	<g:layoutHead/>
	<g:render template="/css"/>
	<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
	<g:javascript library="jquery" plugin="jquery"/>
	<jqui:resources/>
	<g:javascript src="message/check_message.js"></g:javascript>
	<g:javascript src="message/star_message.js"></g:javascript>
	<g:javascript src="application.js"/>
	<g:javascript src="popup.js"/>

</head>
<body>
<g:render template="/tabs"/>
<g:render template="/flash"/>
<div id="main">
	<div id="archive">
		<g:render template="../archive/menu"/>
		<g:render template="list_items"/>
		<g:layoutBody/>
	</div>
</div>
</body>
</html>
