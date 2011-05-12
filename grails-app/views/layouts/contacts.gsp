<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
        <g:layoutHead />
        <link rel="stylesheet" href="${resource(dir:'css',file:'screen.css')}" type="text/css" media="screen, projection"/>
        <link rel="stylesheet" href="${resource(dir:'css',file:'ie.css')}" type="text/css" media="screen, projection"/>
		<!--[if IE]>
			<link rel="stylesheet" href="${resource(dir:'css',file:'print.css')}" type="text/css" media="print"/>
		<![endif]-->
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<g:javascript library="jquery" plugin="jquery"/>
	</head>
	<body>
		<g:render template="/eyebrow"/>
		<g:render template="/flash"/>
		<div id="main">
			<g:render template="menu"/>
			<g:render template="contact_list"/>
			<g:layoutBody />
		</div>
	</body>
</html>