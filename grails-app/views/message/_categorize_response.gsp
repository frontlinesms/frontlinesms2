<div class="dropdown">
	<g:hiddenField id="owner-id" name="owner-id" value="${ownerInstance.id}" />
	<g:hiddenField id="response-id" name="response-id" value="${responseInstance.id}" />
	<h2>Categorise Response</h2>
	<select id="categorise_dropdown">
		<option value="na" class="na">${responseInstance.value}</option>
		<g:each in="${responseList}" status="i" var="r">
			<g:if test="${r.id != responseInstance.id}">
				<option value="btn-${r.id}">${r.value}</option>
			</g:if>
		</g:each>
	</select>
</div>
