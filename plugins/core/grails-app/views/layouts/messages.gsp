<%@ page contentType="text/html;charset=UTF-8"%>
<html>
	<head>
		<title><g:layoutTitle default="Messages"/></title>
		<g:layoutHead/>
		<r:require module="messages"/>
		<r:require module="newMessagesCount"/>
		<g:render template="/includes" plugin="core"/>
		<fsms:i18n keys="poll.reply.text, poll.reply.text1, poll.reply.text2, poll.reply.text3, autoreply.blank.keyword, poll.send.messages.none, autoreply.text.none, wizard.title.new, popup.title.saved, group.join.reply.message, group.leave.reply.message, fmessage.new.info, wizard.fmessage.edit.title, smallpopup.fmessage.delete.title, smallpopup.fmessage.export.title, popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, smallpopup.empty.trash.prompt"/>
		<g:javascript>
			$(function() {  
			   disablePaginationControls();
			});
		</g:javascript>
	</head>
	<body id="messages-tab">
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
					<g:render template="../message/header" plugin="core"/>
					<g:render template="../message/message_list" plugin="core"/>
					<g:layoutBody/>
					<g:render template="../message/footer" plugin="core"/>
				</div>
			    <g:render template="../message/message_details" plugin="core"/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
