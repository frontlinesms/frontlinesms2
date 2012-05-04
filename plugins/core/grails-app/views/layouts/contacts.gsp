<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
		<r:require module="contacts"/>
		<g:render template="/includes" plugin="core"/>
		<g:layoutHead/>
		<fsms:i18n keys="contact.selected.many, smallpopup.group.rename.title, smallpopup.group.edit.title, smallpopup.group.delete.title, smallpopup.customfield.create.title, group.join.reply.message, group.leave.reply.message, popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, smallpopup.delete.prompt, smallpopup.delete.many.prompt, smallpopup.contact.delete.title, smallpopup.contact.export.title, popup.smartgroup.create, popup.help.title, smallpopup.group.title, popup.create, popup.done, popup.edit, popup.ok, smallpopup.ok, smallpopup.rename, wizard.ok, wizard.create, smallpopup.create, smallpopup.export, smallpopup.send, wizard.send"/>
		<r:layoutResources/>
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
				<g:layoutBody/>
				<g:render template="footer" plugin="core"/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
