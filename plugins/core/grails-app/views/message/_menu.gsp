<%@ page contentType="text/html;charset=UTF-8" %>
<div id="sidebar">
	<ul class="main-list"> 
		<li>
			<h3 id="messages-list-title" class="list-title"><g:message code="fmessage.header"/></h3>
			<ul class='sublist' id="messages-submenu">
				<li class="${(messageSection=='inbox')? 'selected':''}">
					<g:link action="inbox"><g:message code="fmessage.inbox"/></g:link>
				</li>
				<li class="${(messageSection=='sent')? 'selected':''}">
					<g:link action="sent"><g:message code="fmessage.sent"/></g:link>
				</li>
				<li class="${(messageSection=='pending')? 'selected':''}">
					<g:link action="pending" class="${hasFailedMessages ? 'pending-send-failed' : ''}"><g:message code="fmessage.pending"/><span id="error-icon"/></g:link>
				</li>
				<li class="${(messageSection=='trash')? 'selected':''}">
					<g:link action="trash"><g:message code="fmessage.trash"/></g:link>
				</li>
			</ul>
		</li>
		<li>
			<h3 class="list-title activities-list-title"><g:message code="activities.header"/></h3>
			<ul class='sublist' id="activities-submenu">
				<g:each in="${activityInstanceList}" status="i" var="a">
					<li class="${a == ownerInstance ? 'selected' : ''}">
						<g:link action="activity" params="[ownerId: a.id]">
							<g:message code="${a.shortName}.title" args="${[a.name]}"/>
						</g:link>
					</li>
				</g:each>
				<li id="create-activity" class="create">
					<g:remoteLink class="btn create" controller="activity" action="create_new_activity" id="create-new-activity" onLoading="showThinking();" onSuccess="hideThinking(); launchMediumPopup(i18n('popup.activity.create'), data, (i18n('popup.next')), chooseActivity);" ><g:message code="activities.create"/></g:remoteLink>
				</li>
			</ul>
		</li>
		<li>
			<h3 id="folders-list-title" class="list-title"><g:message code="folder.header"/></h3>
		 	<ul class='sublist' id='folders-submenu' >
				<g:each in="${folderInstanceList}" status="i" var="f">
					<li class="${f == ownerInstance ? 'selected' : ''}">
						<g:link action="folder" params="[ownerId: f.id]">${f.name}</g:link>
					</li>
				</g:each>
				<li id="create-folder" class="create">
					<g:remoteLink class="btn create" controller="folder" action="create" onLoading="showThinking();" onSuccess="hideThinking(); launchSmallPopup(i18n('smallpopup.folder.title'), data, i18n('smallpopup.create'));">
						<g:message code="folder.create"/>
					</g:remoteLink>
				</li>
			</ul>
		</li>
	</ul>
</div>
