<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Settings"/></title>
		<r:require module="settings"/>
		<fsms:render template="/includes"/>
		<fsms:i18n keys="popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, popup.help.title, connection.edit, connection.add, smallpopup.test.message.title, popup.done, wizard.create, smallpopup.send, popup.ok"/>
		<g:layoutHead/>
	</head>
	<body id="settings-tab">
		<div id="thinking"></div>
		<div id="header">
			<div id="notifications">
				<fsms:render template="/system_notifications"/>
				<fsms:render template="/flash"/>
			</div>
			<fsms:render template="/system_menu"/>
			<fsms:render template="/tabs"/>
		</div>
        <div id="main">
			<fsms:render template="/settings/menu"/>
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
