<ul class="info">
	<h1>
		<g:message code="webconnection.title" args="${[ownerInstance.name]}"/>
	</h1>
	<p class="subtitle">
		<g:message code="webconnection.${ownerInstance?.type}.subtitle" args="${[ownerInstance?.serviceType?.capitalize()]}" id="webconnectionSubtitle"/>
	</p>
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
	<g:if test="${ownerInstance?.keywords}">
		<li id="web_connection_keywords"><g:message code="poll.keywords"/> : ${ownerInstance?.keywords*.value.join(',')}</li>
	</g:if>
</ul>