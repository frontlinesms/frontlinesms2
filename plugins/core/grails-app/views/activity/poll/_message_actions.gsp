<g:hiddenField name="owner-id" value="${ownerInstance.id}"/>
<g:hiddenField name="responseId" value="${responseInstance?.id}"/>
<select class="dropdown extra-msg-btn" name="categorise_dropdown" id="categorise_dropdown" onchange="categorizeClickAction(this)">
	<option value="na" class="na"><g:message code="activity.categorize"/></option>
	<g:each in="${ownerInstance.responses}" status="i" var="r">
		<option value="btn-${r.id}" >${r.value}</option>
	</g:each>
</select>
