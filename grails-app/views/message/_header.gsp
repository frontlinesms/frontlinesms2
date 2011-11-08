<div class="content-header ${messageSection}">
	<g:hiddenField name="starred" value="${params.starred}" />
	<g:hiddenField name="viewingArchive" value="${params.viewingArchive}" />
	<g:hiddenField name="failed" value="${params.failed}" />
	<g:if test="${messageSection == 'sent'}">
		<div class="message-title">
			<img src='${resource(dir:'images/icons',file:'sent.png')}' />
			<h2>${messageSection}</h2>
		</div>
	</g:if>
	<g:elseif test="${messageSection == 'pending'}">
		<div class="message-title">
			<img src='${resource(dir:'images/icons',file:'pending.png')}' />
			<h2>${messageSection}</h2>
		</div>
	</g:elseif>
	<g:elseif test="${messageSection == 'trash'}">
		<div class="message-title">
			<img src='${resource(dir:'images/icons',file:'trash.png')}' />
			<h2>${messageSection}</h2>
		</div>
	</g:elseif>
	<g:elseif test="${messageSection == 'poll'}">
		<div id="poll-title">
			<g:render template="../message/poll_header"/>
		</div>
	</g:elseif>
	<g:elseif test="${messageSection == 'announcement'}">
		<div class="message-title">
			<g:if test="${params.viewingArchive}">
				<g:link controller="archive" action="activityView"> &lt;Back </g:link>
				<img src='${resource(dir:'images/icons',file:'activitiesarchive.png')}' />
			</g:if>
			<g:else>
				<img src='${resource(dir:'images/icons',file:'activities.png')}' />
			</g:else>
			<h2>${ownerInstance?.name}</h2>
		</div>
	</g:elseif>
	<g:elseif test="${messageSection == 'radioShow'}">
		<div class="message-title">
			<img src='${resource(dir:'images/icons',file:'onair.png')}' />
			<h2>On air</h2>
		</div>
	</g:elseif>
	<g:elseif test="${messageSection == 'folder'}">
		<div class="message-title">
			<g:if test="${params.viewingArchive}">
				<g:link controller="archive" action="folder"> &lt;Back </g:link>
			</g:if>
			<g:else>
				<img src='${resource(dir:'images/icons',file:'folders.png')}' />
			</g:else>
			<h2>${ownerInstance?.name}</h2>
		</div>
	</g:elseif>
	<g:else>
		<div class="message-title">
			<img src='${resource(dir:'images/icons',file:'inbox.png')}' />
			<h2>${messageSection}</h2>
		</div>
	</g:else>
	<ol>
		<g:if test="${messageSection == 'trash' && messageInstanceTotal != 0}">
			<li>
				<select id="trash-actions" onchange="launchEmptyTrashConfirmation();">
					<option value="na" class="na">Trash actions...</option>
					<option id="empty-trash" value="empty-trash" >Empty trash</option>
				</select>
			</li>
		</g:if>
		<g:if test="${messageSection != 'trash' && messageSection != 'poll' && messageSection != 'folder' && messageSection != 'announcement'}">
			<li>
				<g:link elementId="export" url="#">
					Export
				</g:link>
			</li>
		</g:if>
		<g:if test="${messageSection == 'announcement'}">
			<li>
				<g:select name="announcement-actions" class="more-actions" from="${['Export', 'Delete announcement']}"
						keys="${['export', 'delete']}"
						noSelection="${['': 'More actions...']}"/>
			</li>
			<g:if test="${!params.viewingArchive}">
				<li class='static_btn'>
					<g:link controller="announcement" action="archive" id="${ownerInstance.id}">Archive Announcement</g:link>
				</li>
			</g:if>
			<g:else>
				<li class='static_btn'>
					<g:link controller="announcement" action="unarchive" id="${ownerInstance.id}">Unarchive Announcement</g:link>
				</li>
			</g:else>
		</g:if>
		<g:if test="${messageSection == 'folder'}">
			<li>
				<g:select name="folder-actions" class="more-actions" from="${['Export', 'Delete folder']}"
						keys="${['export', 'delete']}"
						noSelection="${['': 'More actions...']}"/>
			</li>
			<g:if test="${!params.viewingArchive}">
				<li class='static_btn'>
					<g:link controller="folder" action="archive" id="${ownerInstance.id}">Archive Folder</g:link>
				</li>
			</g:if>
			<g:else>
				<li class='static_btn'>
					<g:link controller="folder" action="unarchive" id="${ownerInstance.id}">Unarchive Folder</g:link>
				</li>
			</g:else>
		</g:if>
		<li>
        	<g:remoteLink controller="quickMessage" action="create" onSuccess="launchMediumWizard('Quick Message', data, 'Send', null, true); addTabValidations();" id="quick_message">
        		<img src='${resource(dir:'images/icons',file:'quickmessage.png')}' />
				Quick message
			</g:remoteLink>
		</li>
	</ol>
	<g:if test="${messageSection == 'poll'}">
		<ol>
			<g:if test="${!params.viewingArchive}">
				<li class='static_btn'>
					<g:link controller="poll" action="archive" id="${ownerInstance.id}">Archive Poll</g:link>
				</li>
			</g:if>
			<g:else>
				<li class='static_btn'>
					<g:link controller="poll" action="unarchive" id="${ownerInstance.id}">Unarchive Poll</g:link>
				</li>
			</g:else>
			<li>
				<g:select name="poll-actions" class="more-actions" from="${['Export', 'Rename activity', 'Delete poll']}"
						keys="${['export', 'rename', 'delete']}"
						noSelection="${['': 'More actions...']}"/>
			</li>
		</ol>
		<ol>
			<li>
				<button id="pollSettings">Show poll details</button>
			</li>
		</ol>
		<div class="poll-details" style="display:none">
			<div id="pollGraph"></div>
		</div>
	</g:if>
</div>