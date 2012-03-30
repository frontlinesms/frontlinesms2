<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Settings"/></title>
		<g:layoutHead />
		<r:require module="settings"/>
		<g:render template="/includes" plugin="core"/>
	</head>
	<body id="settings-tab">
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
				<div class="section-header">
					<h3>Settings</h3>
				</div>
				<g:layoutBody />
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
