<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
		<r:require module="contacts"/>
		<g:render template="/includes" plugin="core"/>
		<g:layoutHead />
	</head>
	<body id="contacts-tab">
		<div id="header">
			<div id="notifications">
				<g:render template="/system_notifications" plugin="core"/>
				<g:render template="/flash" plugin="core"/>
			</div>
			<g:render template="/system_menu" plugin="core"/>
			<g:render template="/tabs" plugin="core"/>
		</div>
		<div id="main">
			<g:render template="menu" plugin="core"/>
			<div id="content">
				<g:render template="header" plugin="core"/>
				<g:render template="contact_list" plugin="core"/>
				<g:layoutBody />
				<g:render template="footer" plugin="core"/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
