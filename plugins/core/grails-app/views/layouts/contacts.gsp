<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
		<r:require module="contacts"/>
		<fsms:render template="/includes"/>
		<g:layoutHead/>
		<fsms:i18n keys="many.selected, contact.selected.many, smallpopup.group.rename.title, smallpopup.group.edit.title, smallpopup.group.delete.title, smallpopup.customfield.create.title, group.join.reply.message, group.leave.reply.message, popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, smallpopup.delete.prompt, smallpopup.delete.many.prompt, smallpopup.contact.delete.title, smallpopup.contact.export.title, popup.smartgroup.create, popup.help.title, smallpopup.group.title, popup.create, popup.done, popup.edit, popup.ok, smallpopup.ok, smallpopup.rename, wizard.ok, wizard.create, smallpopup.create, smallpopup.export, smallpopup.send, wizard.send, contact.delete.many, wizard.send, wizard.send.message.title, message.character.count"/>
		<r:script>
			$(function() {
				disablePaginationControls();
				$(window).resize(new Resizer('#main-list-container', '#main-list-head', '#main-list-foot'));
			});
		</r:script>
		<r:layoutResources/>
	</head>
	<body>
		<fsms:render template="/head"/>
		<div id="body" class="contacts">
			<div id="body-menu">
				<fsms:render template="menu"/>
			</div>
			<div id="main-list-container">
				<div id="main-list-head">
					<fsms:render template="header"/>
				</div>
				<fsms:render template="contact_list"/>
				<div id="main-list-foot">
					<fsms:render template="footer"/>
				</div>
			</div>
			<div id="detail">
				<g:form>
					<g:layoutBody/>
				</g:form>
			</div>
		</div>
		<fsms:render template="/system"/>
	</body>
</html>
