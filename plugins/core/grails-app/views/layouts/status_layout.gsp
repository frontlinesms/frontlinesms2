<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Status"/></title>
		<g:layoutHead/>
		<r:require module="status"/>
		<f:render template="/includes"/>
		<fsms:i18n keys="traffic.sent, traffic.received, traffic.total, popup.cancel, popup.back, smallpopup.cancel, popup.help.title, popup.done"/>
	</head>
	<body id="status-tab">
		<div id="header">
			<div id="notifications">
				<f:render template="/system_notifications"/>
				<f:render template="/flash"/>
			</div>
			<f:render template="/system_menu"/>
			<f:render template="/tabs"/>
		</div>
		<div id="main">
			<div class="content">
				<g:layoutBody/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
