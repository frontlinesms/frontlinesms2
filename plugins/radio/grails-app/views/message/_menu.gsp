<%@ page contentType="text/html;charset=UTF-8" %>
<div id="sidebar">
	<ul class="main-list"> 
		<li>
			<h3 id="messages-list-title" class="list-title">MESSAGES</h3>
			<ul class='sublist' id="messages-submenu">
				<li class="${(messageSection=='inbox')? 'selected':''}">
					<g:link controller="message" action="inbox">Inbox</g:link>
				</li>
				<li class="${(messageSection=='sent')? 'selected':''}">
					<g:link controller="message" action="sent">Sent</g:link>
				</li>
				<li class="${(messageSection=='pending')? 'selected':''}">
					<g:link controller="message" action="pending" class="${hasFailedMessages ? 'send-failed' : ''}">Pending</g:link>
				</li>
				<li class="${(messageSection=='trash')? 'selected':''}">
					<g:link controller="message" action="trash">Trash</g:link>
				</li>
			</ul>
		</li>
		<li>
			<h3 id="shows-list-title" class="list-title">Shows</h3>
			<ul class='sublist' id="shows-submenu">
				<g:each in="${radioShowInstanceList}" status="i" var="s">
					<li class="${s == ownerInstance ? 'selected' : ''}${ownerInstance && ownerInstance in s.activePolls ? 'secondarySelected' : ''}">
						<g:link controller="radioShow" action="radioShow" params="[ownerId: s.id]">
							${s.name}
							<span id="show-${s.id}" class="${s?.isRunning ? 'onAirIsActive' : 'hide'}">On air</span>
						</g:link>
					</li>
					<g:if test="${s.activePolls}">
						<ul id="radio-show-polls">
							<g:each in="${s.activePolls}" status="j" var="p">
								<li class="${p == ownerInstance ? 'selected' : ''}">
									<g:link controller="message" action="poll" params="[ownerId: p.id]">${p.title} poll</g:link>
								</li>
							</g:each>
						</ul>
					</g:if>
				</g:each>
				<li id='create-show' class="create">
					<g:remoteLink class="btn create" controller="radioShow" action="create" onSuccess="launchSmallPopup('Radio Show', data, 'Create')">
						Create new show
					</g:remoteLink>
				</li>
			</ul>
		</li>
		<li>
			<h3 class="list-title activities-list-title">Activities</h3>
			<ul class='sublist' id="activities-submenu">
				<g:each in="${pollInstanceList - radioShowPollInstanceList}" status="i" var="p">
					<li class="${p == ownerInstance ? 'selected' : ''}">
						<g:link controller="message" action="poll" params="[ownerId: p.id]">${p.title} poll</g:link>
					</li>
				</g:each>
				<g:each in="${announcementInstanceList}" status="i" var="a">
					<li class="${a == ownerInstance ? 'selected' : ''}">
						<g:link controller="message" action="announcement" params="[ownerId: a.id]">${a.name} announcement</g:link>
					</li>
				</g:each>
				<li id="create-activity" class="create">
					<g:remoteLink class="btn create" controller="poll" action="create_new_activity" id="create-new-activity" onSuccess="launchMediumPopup('Create New Activity : Select type', data, 'Next', chooseActivity);" >Create new activity</g:remoteLink>
				</li>
			</ul>
		</li>
		<li>
			<h3 id="folders-list-title" class="list-title">Folders</h3>
		 	<ul class='sublist' id='folders-submenu' >
				<g:each in="${folderInstanceList}" status="i" var="f">
					<li class="${f == ownerInstance ? 'selected' : ''}">
						<g:link controller="message" action="folder" params="[ownerId: f.id]">${f.name}</g:link>
					</li>
				</g:each>
				<li id="create-folder" class="create">
					<g:remoteLink class="btn create" controller="folder" action="create" onSuccess="launchSmallPopup('Folder', data, 'Create')">
						Create new folder
					</g:remoteLink>
				</li>
			</ul>
		</li>
	</ul>
</div>
