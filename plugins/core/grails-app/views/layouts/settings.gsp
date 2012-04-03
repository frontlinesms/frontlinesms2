<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Settings"/></title>
		<g:layoutHead />
		<r:require module="settings"/>
		<g:render template="/includes"/>
	</head>
	<body id="settings-tab">
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
				<div class="section-header">
					<h3><g:message code="layout.settings.header" /></h3>
				</div>
				<g:layoutBody />
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
