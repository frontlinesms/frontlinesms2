<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Search"/></title>
		<g:layoutHead />
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources />
		<g:javascript src="application.js"/>
		<g:javascript src="popup.js"></g:javascript>
	</head>
	<body>
		<g:render template="/tabs"/>
		<g:render template="/flash"/>
		<div id="main">
			<h2 id="search-description">
				${searchDescription}
	  		</h2>
			<g:render template="search_menu"/>
			<g:render template="/message/message_list"/>
			<g:layoutBody/>
		</div>
	</body>
</html>
