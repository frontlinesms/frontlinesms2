<%@ page contentType="text/html;charset=UTF-8" %>
<ul class="context-menu" id="messages-menu">
	<li>
		<h2>Create new...</h2>
		<ol id="create-submenu">
				<li id="create-poll">
					<g:remoteLink controller="poll" action="create" onSuccess="launchWizard('Create Poll', data, 1000);">
						Poll
					</g:remoteLink>				
				</li>
				<li id="create-folder">
					<g:remoteLink controller="folder" action="create" onSuccess="launchWizard('Create Folder', data);">
						Folder
					</g:remoteLink>
				</li>
				<li>
					<g:remoteLink controller="radioShow" action="create" onSuccess="launchWizard('Create Show', data)">
						Show
					</g:remoteLink>
				</li>
				<li id="manage-subscription">
					<g:remoteLink controller="group" action="list" onSuccess="launchWizard('Manage Subscription', data);">
						Manage Subscription
					</g:remoteLink>
				</li>
		</ol>
	</li>
	<li>
		<h2>Messages</h2>
		<ol id="messages-submenu">
			<li class="${(messageSection=='inbox')? 'selected':''}">
				<g:link action="inbox">Inbox</g:link>
				(${messageCount['inbox']})
			</li>
			<li class="${(messageSection=='sent')? 'selected':''}">
				<g:link action="sent">Sent</g:link>
				(${messageCount['sent']})
			</li>
			<li class="${(messageSection=='pending')? 'selected':''}">
				<g:link action="pending">Pending</g:link>
				(${messageCount['pending']})
			</li>
			<li class="${(messageSection=='trash')? 'selected':''}">
				<g:link action="trash">Trash</g:link>
			</li>
		</ol>
	</li>
	<li>
		<h2>Activities</h2>
		<ol id="activities-submenu">
			<g:each in="${pollInstanceList}" status="i" var="p">
				<li>
					<g:link action="poll" params="[ownerId: p.id]" class="${p == ownerInstance ? 'selected' : ''}">${p.title}</g:link>
					(${p.countMessages()})
				</li>
			</g:each>
	</li>
	<li>
		<h2>Shows</h2>
		<ol id="shows-submenu">
			<g:each in="${radioShows}" status="i" var="s">
				<li>
					<g:link action="radioShow" params="[ownerId: s.id]" class="${s == ownerInstance ? 'selected' : ''}">${s.name}</g:link>
					(${s.countMessages()})
				</li>
			</g:each>
	</li>
	<li>
		 <h2>Folders</h2>
			<g:each in="${folderInstanceList}" status="i" var="f">
				<li>
					<g:link action="folder" params="[ownerId: f.id]" class="${f == ownerInstance ? 'selected' : ''}">${f.name}</g:link>
					(${f.countMessages()})
				</li>
			</g:each>
		</ol>
	</li>
 </ul>
