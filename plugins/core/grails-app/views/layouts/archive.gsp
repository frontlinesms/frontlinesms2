<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Archive >> <g:layoutTitle/></title>
		<g:layoutHead/>
		<r:require module="archive"/>
		<fsms:render template="/includes"/>
		<fsms:i18n keys="wizard.back, wizard.next, smallpopup.cancel, smallpopup.empty.trash.prompt, fmessage.new.info, fmessage.selected.many, wizard.fmessage.edit.title, smallpopup.fmessage.delete.title, smallpopup.fmessage.export.title, popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, smallpopup.empty.trash.prompt, popup.activity.create, popup.help.title, smallpopup.folder.title, wizard.quickmessage.title, smallpopup.fmessage.rename.title, popup.done, popup.ok, many.selected, fmessage.showpolldetails, fmessage.hidepolldetails,"/>
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
		<div id="body" class="messages">
			<fsms:render template="/archive/menu"/>
			<g:form controller="${params.controller}"
					params="[messageSection: messageSection, ownerId: ownerInstance?.id, messageId: messageInstance?.id, searchId: search?.id]">
				<div id="main-list-container">
					<div id="main-list-head">
						<g:if test="${viewingMessages}">
							<fsms:render template="/message/header"/>
						</g:if>
						<g:else>
							<fsms:render template="header"/>
						</g:else>
					</div>
					<g:if test="${(messageSection == 'activity') && !viewingMessages}">
						<fsms:render template="archived_activity_list"/>
					</g:if>
					<g:elseif test="${messageSection == 'folder' && !viewingMessages}">
						<fsms:render template="archived_folder_list"/>
					</g:elseif>
					<g:else>
						<fsms:render template="/message/message_list"/>
					</g:else>
					<div id="main-list-foot">
						<fsms:render template="/message/footer"/>
					</div>
				</div>
				<g:layoutBody/>
				<div id="detail">
					<fsms:render template="/message/message_details" />
				</div>
			</g:form>
		</div>
		<fsms:render template="/system"/>
	</body>
</html>
