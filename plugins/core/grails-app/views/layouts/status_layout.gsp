<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Status"/></title>
		<g:layoutHead/>
		<r:require module="status"/>
		<fsms:render template="/includes"/>
		<fsms:i18n keys="traffic.sent, traffic.received, traffic.total, popup.cancel, popup.back, smallpopup.cancel, popup.help.title, popup.done, popup.ok"/>
		<r:layoutResources/>
	</head>
	<body id="status-tab">
		<div id="header">
			<div id="notifications">
				<fsms:render template="/system_notifications"/>
				<fsms:render template="/flash"/>
			</div>
			<fsms:render template="/system_menu"/>
			<fsms:render template="/tabs"/>
		</div>
		<div id="main">
			<div class="content">
				<g:layoutBody/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
