<ul class="info">
	<h1>
		<g:message code="customactivity.title" args="${[ownerInstance.name]}"/>
	</h1>
	<li>
		<g:formatDate date="${ownerInstance?.dateCreated}"/>
	</li>
	<li>
		<g:each in="${ownerInstance?.steps}" var="step">
			<p>${step.niceFormat()}</p>
		</g:each>
	</li>
	<g:if test="${ownerInstance?.keywords}">
		<li id="web_connection_keywords"><g:message code="poll.keywords"/> : ${ownerInstance?.keywords*.value.join(',')}</li>
	</g:if>
</ul>

