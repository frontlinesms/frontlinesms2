<div class="content-header ${messageSection}">
	<g:hiddenField name="starred" value="${params.starred}" />
	<g:hiddenField name="viewingArchive" value="${params.viewingArchive}" />
	<g:hiddenField name="failed" value="${params.failed}" />
	<g:if test="${messageSection == 'poll'}">
		<div id="poll-title">
			<g:render template="../message/poll_header"/>
		</div>
	</g:if>
	<g:elseif test="${messageSection == 'folder'}">
		<div class="message-title">
			<g:if test="${params.viewingArchive}">
				<g:link controller="archive" action="folder">&lt; Back</g:link>
			</g:if>
			<g:else>
				<img src='${resource(dir:'images/icons',file:'folders.png')}' />
			</g:else>
			<h2>${ownerInstance?.name}</h2>
		</div>
	</g:elseif>
	<g:elseif test="${messageSection == 'sent'}">
		<div class="message-title">
			<img src='${resource(dir:'images/icons',file:'sent.png')}' />
			<h2>${messageSection}</h2>
		</div>
	</g:elseif>
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
	<g:elseif test="${messageSection == 'radioShow'}">
		<div class="message-title">
			<img src='${resource(dir:'images/icons',file:'onair.png')}' />
			<h2>On air</h2>
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
		<g:if test="${messageSection != 'trash' && messageSection != 'poll' && messageSection != 'folder'}">
			<li>
				<g:link elementId="export" url="#">
					Export
				</g:link>
			</li>
		</g:if>
		<g:if test="${messageSection == 'folder'}">
			<li>
				<g:select name="folder-actions" from="${['Export', 'Delete folder']}"
						keys="${['export', 'deleteAction']}"
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
				<g:select name="poll-actions" from="${['Export', 'Rename activity', 'Delete activity']}"
						keys="${['export', 'renameActivity', 'deleteAction']}"
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