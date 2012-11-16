<ul class="info">
	<h1>
		<g:message code="${ownerInstance.shortName}.title" args="${[ownerInstance.name]}"/>
	</h1>
	<li>
		<g:formatDate date="${ownerInstance.dateCreated}"/>
	</li>
	<li>
		${ownerInstance.sentMessageText}
	</li>
	<g:if test="${ownerInstance?.keywords}">
		<li id="web_connection_keywords"><g:message code="poll.keywords"/> : ${ownerInstance?.keywords*.value.join(',')}</li>
	</g:if>
</ul>

