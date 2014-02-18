<%@ page contentType="text/html;charset=UTF-8" import="frontlinesms2.TextMessage" %>
<%@ page contentType="text/html;charset=UTF-8" import="frontlinesms2.MissedCall" %>
<%@ page contentType="text/html;charset=UTF-8" import="frontlinesms2.FrontlinesyncFconnection" %>
<fsms:menu class="messages">
	<fsms:submenu code="fmessage.header" class="messages">
		<fsms:menuitem class="" selected="${messageSection=='inbox'}" controller="message" action="inbox" code="fmessage.section.inbox" entitytype="inbox">
			<fsms:unreadCount unreadCount="${TextMessage.countUnreadMessages()}"/>
		</fsms:menuitem>
		<fsms:menuitem class="" selected="${messageSection=='sent'}" controller="message" action="sent" code="fmessage.section.sent"/>
		<fsms:menuitem class="" selected="${messageSection=='pending'}" controller="message" action="pending" code="fmessage.section.pending" entitytype="pending">
			<fsms:pendingCount pendingCount="${TextMessage.pendingAndNotFailed.count()}"/>
		</fsms:menuitem>
		<fsms:menuitem class="" selected="${messageSection=='trash'}" controller="message" action="trash" code="fmessage.section.trash"/>
	</fsms:submenu>

	<g:if test="${FrontlinesyncFconnection.count() || MissedCall.count()}">
		<fsms:submenu code="missedCall.header" class="missedCalls">
			<fsms:menuitem class="" selected="${params.controller=='missedCall'}" controller="missedCall" action="inbox" code="missedCall.section.inbox" entitytype="inbox">
				<fsms:unreadCount unreadCount="${MissedCall.countUnreadMessages()}"/>
			</fsms:menuitem>
		</fsms:submenu>
	</g:if>

	<fsms:submenu code="activities.header" class="activities">
		<g:each in="${activityInstanceList}" status="i" var="a">
			<fsms:menuitem class="" selected="${a == ownerInstance}" controller="message" action="activity" code="${a.shortName.toLowerCase()}.title" msgargs="${[a.name]}" params="[ownerId: a.id]" entitytype="activity" entityid="${a.id}">
				<fsms:unreadCount unreadCount="${TextMessage.countUnreadMessages(a)}"/>
			</fsms:menuitem>
		</g:each>
		<fsms:menuitem bodyOnly="true" class="create">
			<fsms:popup class="btn create" controller="activity" action="create_new_activity" id="create-new-activity" popupCall="mediumPopup.launchMediumPopup(i18n('popup.activity.create'), data, (i18n('action.next')), chooseActivity);">
				<g:message code="activities.create"/>
			</fsms:popup>
		</fsms:menuitem>
	</fsms:submenu>
	<fsms:submenu code="folder.header" class="folders">
		<g:each in="${folderInstanceList}" status="i" var="f">
			<fsms:menuitem class="" selected="${f == ownerInstance}" controller="message" action="folder" string="${f.name}" params="[ownerId: f.id]" entitytype="folder" entityid="${f.id}">
				<fsms:unreadCount unreadCount="${TextMessage.countUnreadMessages(f)}"/>
			</fsms:menuitem>
		</g:each>
		<fsms:menuitem bodyOnly="true" class="create">
			<fsms:popup class="btn create" controller="folder" action="create" popupCall="launchSmallPopup(i18n('smallpopup.folder.create.title'), data, i18n('action.create'),'validate')">
				<g:message code="folder.create"/>
			</fsms:popup>
		</fsms:menuitem>
	</fsms:submenu>
</fsms:menu>

