<ul class="info">
	<h1>
		<g:message code="webConnection.title" args="${[ownerInstance.name]}"/>
	</h1>
	<li>
		<g:formatDate date="${ownerInstance?.dateCreated}"/>
	</li>
	<li>
		<span id="web_connection_url">${ownerInstance?.url}</span>
		<span id="web_connection_method">(${ownerInstance.httpMethod?.toString()})</span>
	</li>
</ul>

