<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Folder"/></title>
        <g:layoutHead />
		<g:render template="/css"/>
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />

<!--	Compass Stylesheets	-->
		<link href="${resource(dir:'css',file:'screen.css')}" media="screen, projection" rel="stylesheet" type="text/css" />
		<link href="${resource(dir:'css',file:'print.css')}" media="print" rel="stylesheet" type="text/css" />
		<!--[if lt IE 8]>
		  <link href="${resource(dir:'css',file:'ie.css')}" media="screen, projection" rel="stylesheet" type="text/css" />
		<![endif]-->


		<g:javascript library="jquery" plugin="jquery"/>
	</head>
	<body>
		<g:render template="/tabs"/>
		<g:render template="/flash"/>
		<div id="main">
			<g:layoutBody />
		</div>
	</body>
</html>