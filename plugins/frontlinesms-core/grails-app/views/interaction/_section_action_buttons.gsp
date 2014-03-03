<ul class="buttons">
	<li><fsms:quickMessage class="section-action-button btn"/></li>
	<g:if test="${!(messageSection in ['trash', 'folder', 'activity', 'missedCalls'])}">
		<li><g:link elementId="export" url="#" class="btn">
			<g:message code="fmessage.export"/>
		</g:link></li>
	</g:if>
	<g:elseif test="${messageSection == 'trash' && interactionInstanceTotal != 0}">
		<li class="trash">
			<select class="dropdown" id="trash-actions" onchange="launchEmptyTrashConfirmation(); selectmenuTools.snapback(this)">
				<option value="na" class="na"><g:message code="fmessage.trash.actions"/></option>
				<option id="empty-trash" value="empty-trash" ><g:message code="fmessage.trash.empty"/></option>
			</select>
		</li>
	</g:elseif>
</ul>
