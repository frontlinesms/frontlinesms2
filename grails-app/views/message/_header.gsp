<div class="section-actions ${messageSection}" id="inbox-actions">
	<g:hiddenField name="starred" value="${params.starred}" />
	<g:hiddenField name="viewingArchive" value="${params.viewingArchive}" />
	<g:hiddenField name="failed" value="${params.failed}" />
	<g:if test="${messageSection == 'poll'}">
		<div id="poll-title">
			<g:render template="../message/poll_header"/>
		</div>
	</g:if>
	<g:elseif test="${messageSection == 'folder'}">
			<g:if test="${params.viewingArchive}">
				<g:link controller="archive" action="folder">&lt; Back</g:link>
			</g:if>
			<g:else>
				<img src='${resource(dir:'images/icons',file:'folders.png')}' />
			</g:else>
			<h2>${ownerInstance?.name}</h2>
	</g:elseif>
	<g:elseif test="${messageSection == 'sent'}">
	    <h3>${messageSection}</h3>
	</g:elseif>
	<g:elseif test="${messageSection == 'pending'}">
			<h2>${messageSection}</h2>
	</g:elseif>
	<g:elseif test="${messageSection == 'trash'}">
			<h2>${messageSection}</h2>
	</g:elseif>
	<g:elseif test="${messageSection == 'radioShow'}">
			<h2>On air</h2>
	</g:elseif>
	<g:else>
	    <h3>${messageSection}</h3>
	</g:else>
	<div class="section-actions-buttons">
		<g:if test="${messageSection == 'trash' && messageInstance != null}">
			<li>
				<select id="trash-actions" onchange="launchEmptyTrashConfirmation();">
					<option value="na" class="na">Trash actions...</option>
					<option id="empty-trash" value="empty-trash" >Empty trash</option>
				</select>
			</li>
		</g:if>
		<g:if test="${messageSection != 'trash' && messageSection != 'poll'}">
				<g:link elementId="export" url="#" class="btn">
					Export
				</g:link>
		</g:if>
		<g:if test="${messageSection == 'folder'}">
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
        	<g:remoteLink controller="quickMessage" action="create" onSuccess="launchMediumWizard('Quick Message', data, 'Send', null, true); addTabValidations();" id="quick_message" class="section-action-button btn">
				Quick message
			</g:remoteLink>
	</div>
	<g:if test="${messageSection == 'poll'}">
			<g:if test="${!params.viewingArchive}">
					<g:link controller="poll" action="archive" id="${ownerInstance.id}">Archive Poll</g:link>
			</g:if>
			<g:else>
					<g:link controller="poll" action="unarchive" id="${ownerInstance.id}">Unarchive Poll</g:link>
			</g:else>
				<g:select name="poll-actions" from="${['Export', 'Rename activity']}"
						keys="${['export', 'renameActivity']}"
						noSelection="${['': 'More actions...']}"/>
				<button id="pollSettings">Show poll details</button>
		<div class="poll-details" style="display:none">
			<div id="pollGraph"></div>
		</div>
	</g:if>
</div>
