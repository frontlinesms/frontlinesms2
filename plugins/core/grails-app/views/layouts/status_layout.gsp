<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Status"/></title>
		<g:layoutHead />
		<r:require module="status"/>
		<g:render template="/includes" plugin="core"/>
		<fsms:i18n keys="traffic.sent, traffic.received, traffic.total, popup.cancel, popup.back, smallpopup.cancel, popup.help.title"/>
	</head>
	<body id="status-tab">
		<div id="header">
			<div id="notifications">
				<g:render template="/system_notifications" plugin="core"/>
				<g:render template="/flash" plugin="core"/>
			</div>
			<g:render template="/system_menu" plugin="core"/>
			<g:render template="/tabs" plugin="core"/>
		</div>
		<div id="main">
			<div class="content">
				<g:layoutBody />
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
