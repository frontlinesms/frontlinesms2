<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
		<r:require module="contacts"/>
		<fsms:render template="/includes"/>
		<g:layoutHead/>
		<fsms:i18n keys="many.selected, contact.selected.many, smallpopup.group.rename.title, smallpopup.group.edit.title, smallpopup.group.delete.title, smallpopup.customfield.create.title, group.join.reply.message, group.leave.reply.message, popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, smallpopup.delete.prompt, smallpopup.delete.many.prompt, smallpopup.contact.delete.title, smallpopup.contact.export.title, popup.smartgroup.create, popup.help.title, smallpopup.group.title, popup.create, popup.done, popup.edit, popup.ok, smallpopup.ok, smallpopup.rename, wizard.ok, wizard.create, smallpopup.create, smallpopup.export, smallpopup.send, wizard.send, contact.delete.many, wizard.send"/>
		<r:layoutResources/>
	</head>
	<body id="contacts-tab">
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
			<fsms:render template="menu"/>
			<div id="content">
				<fsms:render template="header"/>
				<g:form name="details">
					<fsms:render template="contact_list"/>
					<g:layoutBody/>
					<fsms:render template="footer"/>
				</g:form>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
