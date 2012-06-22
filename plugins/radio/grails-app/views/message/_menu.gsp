<%@ page contentType="text/html;charset=UTF-8" %>
<div id="body-menu">
	<ul> 
		<li class="messages">
			<h3><g:message code="fmessage.header"/></h3>
			<ul class="submenu">
				<li class="${(messageSection=='inbox')? 'selected':''}">
					<g:link controller="message" action="inbox"><g:message code="fmessage.section.inbox"/></g:link>
				</li>
				<li class="${(messageSection=='sent')? 'selected':''}">
					<g:link controller="message" action="sent"><g:message code="fmessage.section.sent"/></g:link>
				</li>
				<li class="${(messageSection=='pending')? 'selected':''}">
					<g:link controller="message" action="pending" class="${hasFailedMessages ? 'failures' : ''}"><g:message code="fmessage.section.pending"/></g:link>
				</li>
				<li class="${(messageSection=='trash')? 'selected':''}">
					<g:link controller="message" action="trash"><g:message code="fmessage.section.trash"/></g:link>
				</li>
			</ul>
		</li>
		<li class="radioShows>
			<h3 id="shows-list-title" class="list-title">Shows</h3>
			<ul class='sublist' id="shows-submenu">
				<g:each in="${radioShowInstanceList}" status="i" var="s">
					<li class="${s == ownerInstance ? 'selected' : ''}${ownerInstance && ownerInstance in s.activeActivities ? 'secondarySelected' : ''}">
						<g:link controller="radioShow" action="radioShow" params="[ownerId: s.id]">
							${s.name}
							<span id="show-${s.id}" class="${s?.isRunning ? 'onAirIsActive' : 'hide'}"><g:message code="radio.show.onair"/></span>
						</g:link>
					</li>
					<g:if test="${s.activeActivities}">
						<ul id="radio-show-activities">
							<g:each in="${s.activeActivities}" status="j" var="a">
								<li class="${a == ownerInstance ? 'selected' : ''}">
									<g:link controller="message" action="activity" params="[ownerId: a.id]"><g:message code="${a.shortName}.title" args="${[a.name]}"/></g:link>
								</li>
							</g:each>
						</ul>
					</g:if>
				</g:each>
				<li id='create-show' class="create">
					<g:remoteLink class="btn create" controller="radioShow" action="create" onLoading="showThinking();" onSuccess="hideThinking(); launchSmallPopup('Radio Show', data, 'Create')">
						<g:message code="radio.show.create.new" />
					</g:remoteLink>
				</li>
			</ul>
		</li>
		<li class="activities">
			<h3><g:message code="activities.header"/></h3>
			<ul class="submenu">
				<g:each in="${activityInstanceList - radioShowActivityInstanceList}" status="i" var="a">
					<li class="${a == ownerInstance ? 'selected' : ''}">
						<g:link controller="message" action="activity" params="[ownerId: a.id]">
							<g:message code="${a.shortName}.title" args="${[a.name]}"/>
						</g:link>
					</li>
				</g:each>
				<li class="create">
					<g:remoteLink class="btn create" controller="activity" action="create_new_activity" id="create-new-activity" onLoading="showThinking();" onSuccess="hideThinking(); launchMediumPopup(i18n('popup.activity.create'), data, (i18n('action.next')), chooseActivity);">
						<g:message code="activities.create"/>
					</g:remoteLink>
				</li>
			</ul>
		</li>
		<li class="folders">
			<h3><g:message code="folder.header"/></h3>
		 	<ul class="submenu">
				<g:each in="${folderInstanceList}" status="i" var="f">
					<li class="${f == ownerInstance ? 'selected' : ''}">
						<g:link controller="message" action="folder" params="[ownerId: f.id]">${f.name}</g:link>
					</li>
				</g:each>
				<li class="create">
					<g:remoteLink class="btn create" controller="folder" action="create" onLoading="showThinking();" onSuccess="hideThinking(); launchSmallPopup(i18n('smallpopup.folder.title'), data, i18n('action.create'));">
						<g:message code="folder.create"/>
					</g:remoteLink>
				</li>
			</u
	</ul>
</div>
