<ul class="info">
	<h1>
		<g:message code="webConnection.title" args="${[ownerInstance.name]}"/>
	</h1>
	<li>
		<g:formatDate date="${ownerInstance?.dateCreated}"/>
	</li>
	<li>
		${ownerInstance.connection?.url} (${ownerInstance.connection?.httpMethod?.toString()})
	</li>
	<li>
		<g:message code="fmessage.activity.sentmessage" args="${ [sentMessageCount] }"/>
	</li>
</ul>

