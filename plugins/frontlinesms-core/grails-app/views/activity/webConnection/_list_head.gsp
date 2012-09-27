<ul class="info">
	<h1>
		<g:message code="webConnection.title" args="${[ownerInstance.name]}"/>
	</h1>
	<g:message code="webConnection.${ownerInstance?.type}.subtitle" id="webConnectionSubtitle"/>
	<li>
		<g:formatDate date="${ownerInstance?.dateCreated}"/>
	</li>
	<li>
		<g:if test="${ownerInstance?.type == 'ushahidi'}">
			<span id="web_connection_url">${ownerInstance?.url}</span>
			<span id="web_connection_method">(${ownerInstance.httpMethod?.toString()})</span>
		</g:if>
		<g:if test="${ownerInstance?.type == 'generic'}">
			<span id="web_connection_url">${ownerInstance?.url}</span>
			<span id="web_connection_method">(${ownerInstance.httpMethod?.toString()})</span>
		</g:if>
	</li>
</ul>

