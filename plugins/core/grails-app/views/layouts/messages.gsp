<%@ page contentType="text/html;charset=UTF-8"%>
<html>
	<head>
		<title><g:layoutTitle default="Messages"/></title>
		<g:layoutHead/>
		<r:require module="messages"/>
		<r:require module="newMessagesCount"/>
		<g:render template="/includes" plugin="core"/>
		<fsms:i18n keys="poll.label, autoreply.label, announcement.label, poll.reply.text, poll.reply.text1, poll.reply.text2, poll.reply.text3, autoreply.blank.keyword, poll.send.messages.none, autoreply.text.none, wizard.title.new, popup.title.saved, group.join.reply.message, group.leave.reply.message, fmessage.new.info, wizard.fmessage.edit.title, smallpopup.fmessage.delete.title, smallpopup.fmessage.export.title, popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, smallpopup.empty.trash.prompt, popup.activity.create, popup.help.title, smallpopup.folder.title, wizard.quickmessage.title, smallpopup.fmessage.rename.title,  popup.next, fmessage.export, popup.done, popup.edit, popup.ok, smallpopup.ok, smallpopup.rename, smallpopup.delete, smallpopup.export, wizard.ok, wizard.create, smallpopup.done, smallpopup.create, smallpopup.send, wizard.send, wizard.send"/>
		<g:javascript>
			$(function() {  
			   disablePaginationControls();
			});
		</g:javascript>
	</head>
	<body id="messages-tab">
		<div id="thinking"></div>
		<div id="header">
			<div id="notifications">
				<g:render template="/system_notifications" plugin="core"/>
				<g:render template="/flash" plugin="core"/>
			</div>
			<g:render template="/system_menu" plugin="core"/>
			<g:render template="/tabs" plugin="core"/>
		</div>
		<div id="main">
			<g:render template="../message/menu" plugin="${grailsApplication.config.frontlinesms2.plugin}"/>
			<div id="content">
				<div id="message-list" class="${(messageSection == 'inbox' || messageSection == 'sent' || messageSection == 'pending' || messageSection == 'trash' || messageSection == 'radioShow' || messageSection == 'folder' || params.action == 'no_search') ? '' : 'tall-header'}">
					<g:render template="header" />
					<g:render template="message_list" />
					<g:layoutBody/>
					<g:render template="/message/footer" plugin="core"/>
				</div>
			    <g:render template="/message/message_details" plugin="core"/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
