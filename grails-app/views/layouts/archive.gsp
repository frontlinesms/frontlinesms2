<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:layoutTitle default="Archive"/></title>
	<g:layoutHead/>
	<g:render template="/css"/>
	<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
	<g:javascript library="jquery" plugin="jquery"/>
	<jqui:resources/>
</head>
<body>
<g:render template="/tabs"/>
<g:render template="/flash"/>
<div id="main">
	<div id="content">
		<g:render template="../archive/menu"/>
		<g:render template="message_list"/>
		<g:layoutBody/>
	</div>
</div>
</body>
</html>
