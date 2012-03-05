<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Status"/></title>
		<g:layoutHead />
		<r:require module="status"/>
		<g:render template="/includes"/>
	</head>
	<body id="status-tab">
		<div id="header">
			<div id="notifications">
				<g:render template="/system_notifications"/>
				<g:render template="/flash"/>
			</div>
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
		</div>
		<div id="main">
			<div class="content">
				<g:layoutBody />
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
