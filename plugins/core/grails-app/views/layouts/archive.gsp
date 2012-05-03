<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Archive"/></title>
		<g:layoutHead/>
		<r:require module="archive"/>
		<f:render template="/includes"/>
		<fsms:i18n keys="wizard.back, wizard.next, smallpopup.cancel, smallpopup.empty.trash.prompt, fmessage.new.info, wizard.fmessage.edit.title, smallpopup.fmessage.delete.title, smallpopup.fmessage.export.title, popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, smallpopup.empty.trash.prompt, popup.activity.create, popup.help.title, smallpopup.folder.title, wizard.quickmessage.title, smallpopup.fmessage.rename.title, popup.done"/>
		<g:javascript>
			$(function() {  
			   disablePaginationControls();
			});
		</g:javascript>
	</head>
	<body id="archive-tab">
		<div id="header">
			<div id="notifications">
				<f:render template="/system_notifications"/>
				<f:render template="/flash"/>
			</div>
			<f:render template="/system_menu"/>
			<f:render template="/tabs"/>
        </div>
		<div id="main" class="main">
			<f:render template="/archive/menu"/>
			<div id="content">
				<div id="message-list" class="${(messageSection == 'inbox' || messageSection == 'sent' || messageSection == 'pending' || messageSection == 'trash' || messageSection == 'radioShow' || messageSection == 'folder' || params.action == 'no_search') ? '' : 'tall-header'}">
					<g:if test="${viewingMessages}">
						<f:render template="/message/header"/>
					</g:if>
					<g:else>
						<f:render template="header"/>
					</g:else>
					<g:if test="${(messageSection == 'activity') && !viewingMessages}">
						<f:render template="archived_activity_list"/>
					</g:if>
					<g:elseif test="${messageSection == 'folder' && !viewingMessages}">
						<f:render template="archived_folder_list"/>
					</g:elseif>
					<g:else>
						<f:render template="/message/message_list"/>
					</g:else>
					<g:layoutBody/>
					<f:render template="/message/footer"/>
				</div>
				<f:render template="/message/message_details" />
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
