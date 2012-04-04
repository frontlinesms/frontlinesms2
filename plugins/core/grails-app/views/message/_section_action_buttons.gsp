<ul class="section-header-buttons button-list">
	<g:if test="${messageSection == 'trash' && messageInstanceTotal != 0}">
		<li class="trash">
			<select class="dropdown" id="trash-actions" onchange="launchEmptyTrashConfirmation();">
				<option value="na" class="na"><g:message code="fmessage.trash.actions" /></option>
				<option id="empty-trash" value="empty-trash" ><g:message code="fmessage.trash.empty" /></option>
			</select>
		</li>
	</g:if>
	<g:if test="${!(messageSection in ['trash', 'folder', 'activity'])}">
		<li><g:link elementId="export" url="#" class="btn">
			<g:message code="fmessage.export" />
		</g:link></li>
	</g:if>
	<g:if test="${messageSection == 'folder'}">
		<li>
			<g:select name="more-actions" class="activity-btn dropdown more-actions" from="${['Export', 'Delete ' + messageSection]}"
					keys="${['export', 'delete']}"
					noSelection="${['': g.message(code:'fmessage.moreactions')]}"/>
		</li>
		<g:if test="${params.controller!='archive'}">
			<li>
				<g:link class="btn" controller="$messageSection" action="archive" id="${ownerInstance?.id}">
					<g:message code="fmessage.activity.archive" args="${ [messageSection] }" />
				</g:link>
			</li>
		</g:if>
		<g:else>
			<li>
				<g:link class="btn" controller="$messageSection" action="unarchive" id="${ownerInstance?.id}">
					<g:message code="fmessage.unarchive" args="${ [messageSection] }" />
				</g:link>
			</li>
		</g:else>
	</g:if>
       <li><g:remoteLink class="section-action-button btn" controller="quickMessage" action="create" onSuccess="launchMediumWizard('Quick Message', data, 'Send', true);" id="quick_message">
			<div id="quick-message">
				<g:message code="fmessage.quickmessage" />
			</div>
		</g:remoteLink></li>
</ul>
