<%@ page contentType="text/html;charset=UTF-8" %>
<div id="sidebar">
<ul class="main-list"> 
	<li>

		<h3>MESSAGES</h3>
		<ul class='sublist' id="messages-submenu">
			<li class="${(messageSection=='inbox')? 'selected':''}">
				<g:link action="inbox">Inbox</g:link>
			</li>
			<li class="${(messageSection=='sent')? 'selected':''}">
				<g:link action="sent">Sent</g:link>
			</li>
			<li class="${(messageSection=='pending')? 'selected':''}">
				<g:link action="pending" class="${hasFailedMessages ? 'send-failed' : ''}">Pending</g:link>
			</li>
			<li class="${(messageSection=='trash')? 'selected':''}">
				<g:link action="trash">Trash</g:link>
			</li>
		</ul>
	</li>
	<li>

		<h3>Polls</h3>
		<ul class='sub-menu' id="activities-submenu">
			<g:each in="${pollInstanceList}" status="i" var="p">
				<li class="${p == ownerInstance ? 'selected' : ''}">
					<g:link action="poll" params="[ownerId: p.id]">${p.title}</g:link>
				</li>
			</g:each>
			<li class='create' id="create-activity">
				<g:link url="#" elementId="create-new-activity">Create new activity</g:link>
			</li>
		</ul>
	</li>
	<li>

		<h2>Shows</h2>
		<ul class='sub-menu' id="shows-submenu">
			<g:each in="${radioShows}" status="i" var="s">
				<li class="${s == ownerInstance ? 'selected' : ''}">
					<g:link action="radioShow" params="[ownerId: s.id]">${s.name}</g:link>
				</li>
			</g:each>
			<li class="create" id='create-show'>
				<g:remoteLink controller="radioShow" action="create" onSuccess="launchSmallPopup('Radio Show', data, 'Create')">
					Create new show
				</g:remoteLink>
			</li>
		</ul>
	</li>
	<li>

		 <h2>Folders</h2>
	 	<ul class='sub-menu' id='folders-submenu' >
			<g:each in="${folderInstanceList}" status="i" var="f">
				<li class="${f == ownerInstance ? 'selected' : ''}">
					<g:link action="folder" params="[ownerId: f.id]">${f.name}</g:link>
				</li>
			</g:each>
			<li class='create' id="create-folder">
				<g:remoteLink controller="folder" action="create" onSuccess="launchSmallPopup('Folder', data, 'Create');">
					Create new folder
				</g:remoteLink>
			</li>
		</ul>
	</li>
 </ul>
</div>
<script>
$("#create-new-activity").bind('click', function() {
	$.ajax({
		type:'GET',
		dataType: "html",
		url: url_root + 'create_new_activity.gsp',
		success: function(data) {
			launchMediumPopup('Create New Activity : Select type', data, 'Next');
			addValidations();
		}
	});

});

</script>
