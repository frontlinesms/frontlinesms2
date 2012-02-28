<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
		<g:render template="/includes"/>
		<r:require module="contacts"/>
		<g:layoutHead />
	</head>
	<body id="contacts-tab">
		<div id="header">
			<div id="notifications">
				<g:render template="/system_notifications"/>
				<g:render template="/flash"/>
			</div>
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
		</div>
		<div id="main">
			<g:render template="menu"/>
			<div id="content">
				<g:render template="header"/>
				<g:layoutBody />
				<g:render template="footer"/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
