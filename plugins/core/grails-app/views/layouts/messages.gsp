<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title><g:layoutTitle default="Messages"/></title>
		<g:layoutHead/>
		<r:require module="messages"/>
		<r:require module="newMessagesCount"/>
		<fsms:render template="/includes"/>
		<fsms:i18n keys="many.selected, poll.label, autoreply.label,
				announcement.label, poll.reply.text,
				poll.reply.text1, poll.reply.text2,
				poll.reply.text3, autoreply.blank.keyword,
				poll.send.messages.none, autoreply.text.none,
				wizard.title.new, popup.title.saved,
				group.join.reply.message,
				group.leave.reply.message, fmessage.new.info,
				fmessage.selected.many,
				wizard.fmessage.edit.title,
				smallpopup.fmessage.delete.title,
				smallpopup.fmessage.export.title,
				popup.cancel, popup.back, wizard.cancel,
				wizard.back, wizard.next, smallpopup.cancel,
				smallpopup.empty.trash.prompt,
				popup.activity.create, popup.help.title,
				smallpopup.folder.title,
				wizard.quickmessage.title,
				smallpopup.fmessage.rename.title,
				popup.next, fmessage.export, popup.done,
				popup.edit, popup.ok, smallpopup.ok,
				smallpopup.rename, smallpopup.delete,
				smallpopup.export, wizard.ok, wizard.create,
				smallpopup.done, smallpopup.create,
				smallpopup.send, wizard.send, popup.ok,
				message.character.count, fmessage.showpolldetails,
				fmessage.hidepolldetails, poll.reply.text5,
				poll.reply.text6, smallpopup.recipients.title, magicwand.title, folder.create.failed"/>
		<r:script>
			$(function() {  
				disablePaginationControls();
				$(window).resize(new Resizer('#list-container', '#list-head', '#list-foot'));
			});
		</r:script>
		<r:layoutResources/>
	</head>
	<body>
		<div id="head">
			<fsms:render template="/tabs"/>
		</div>
		<div id="body" class="messages">
			<fsms:render template="/message/menu"/>
			<g:form controller="${params.controller}"
					params="[messageId: messageInstance?.id, searchId: search?.id]">
				<g:hiddenField name="messageSection" value="${messageSection}"/>
				<g:hiddenField name="ownerId" value="${ownerInstance?.id}"/>
				<div id="list-container">
					<div id="list-head">
						<fsms:render template="/message/header"/>
					</div>
					<fsms:render template="/message/message_list"/>
					<div id="list-foot">
						<fsms:render template="/message/footer"/>
					</div>
				</div>
				<div id="detail">
					<fsms:render template="/message/message_details"/>
				</div>
			</g:form>
		</div>
		<fsms:render template="/system"/>
	</body>
</html>

