<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Settings"/></title>
		<r:require module="settings"/>
		<f:render template="/includes"/>
		<fsms:i18n keys="popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, popup.help.title, connection.edit, connection.add, smallpopup.test.message.title, popup.done, wizard.create, smallpopup.send, popup.ok"/>
		<g:layoutHead/>
	</head>
	<body id="settings-tab">
		<div id="header">
			<div id="notifications">
				<f:render template="/system_notifications"/>
				<f:render template="/flash"/>
			</div>
			<f:render template="/system_menu"/>
			<f:render template="/tabs"/>
		</div>
        <div id="main">
			<f:render template="/settings/menu"/>
			<div id="content">
				<div class="section-header">
					<h3 class="settings"><g:message code="layout.settings.header"/></h3>
				</div>
				<g:layoutBody/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
