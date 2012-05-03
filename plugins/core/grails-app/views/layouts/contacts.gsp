<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
		<r:require module="contacts"/>
		<f:render template="/includes"/>
		<g:layoutHead/>
		<fsms:i18n keys="contact.selected.many, smallpopup.group.rename.title, smallpopup.group.edit.title, smallpopup.group.delete.title, smallpopup.customfield.create.title, group.join.reply.message, group.leave.reply.message, popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, smallpopup.delete.prompt, smallpopup.delete.many.prompt, smallpopup.contact.delete.title, smallpopup.contact.export.title, popup.smartgroup.create, popup.help.title, smallpopup.group.title, popup.create, popup.done, popup.edit, popup.ok, smallpopup.ok, smallpopup.rename, wizard.ok, wizard.create, smallpopup.create, smallpopup.export, smallpopup.send, wizard.send, contact.delete.many, wizard.send"/>
	</head>
	<body id="contacts-tab">
		<div id="thinking"></div>
		<div id="header">
			<div id="notifications">
				<f:render template="/system_notifications"/>
				<f:render template="/flash"/>
			</div>
			<f:render template="/system_menu"/>
			<f:render template="/tabs"/>
		</div>
		<div id="main">
			<f:render template="menu"/>
			<div id="content">
				<f:render template="header"/>
				<f:render template="contact_list"/>
				<g:layoutBody/>
				<f:render template="footer"/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
