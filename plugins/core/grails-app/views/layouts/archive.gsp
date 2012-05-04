<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Archive"/></title>
		<g:layoutHead/>
		<r:require module="archive"/>
		<fsms:render template="/includes"/>
		<fsms:i18n keys="wizard.back, wizard.next, smallpopup.cancel, smallpopup.empty.trash.prompt, fmessage.new.info, wizard.fmessage.edit.title, smallpopup.fmessage.delete.title, smallpopup.fmessage.export.title, popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, smallpopup.empty.trash.prompt, popup.activity.create, popup.help.title, smallpopup.folder.title, wizard.quickmessage.title, smallpopup.fmessage.rename.title, popup.done"/>

		<r:script>
			$(function() {  
			   disablePaginationControls();
			});
		</r:script>
		<r:layoutResources/>
	</head>
	<body id="archive-tab">
		<div id="header">
			<div id="notifications">
				<fsms:render template="/system_notifications"/>
				<fsms:render template="/flash"/>
			</div>
			<fsms:render template="/system_menu"/>
			<fsms:render template="/tabs"/>
        </div>
		<div id="main" class="main">
			<fsms:render template="/archive/menu"/>
			<div id="content">
				<div id="message-list" class="${(messageSection == 'inbox' || messageSection == 'sent' || messageSection == 'pending' || messageSection == 'trash' || messageSection == 'radioShow' || messageSection == 'folder' || params.action == 'no_search') || !viewingMessages ? '' : 'tall-header'}">
					<g:if test="${viewingMessages}">
						<fsms:render template="/message/header"/>
					</g:if>
					<g:else>
						<fsms:render template="header"/>
					</g:else>
					<g:if test="${(messageSection == 'activity') && !viewingMessages}">
						<fsms:render template="archived_activity_list"/>
					</g:if>
					<g:elseif test="${messageSection == 'folder' && !viewingMessages}">
						<fsms:render template="archived_folder_list"/>
					</g:elseif>
					<g:else>
						<fsms:render template="/message/message_list"/>
					</g:else>
					<g:layoutBody/>
					<fsms:render template="/message/footer"/>
				</div>
				<fsms:render template="/message/message_details"/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
