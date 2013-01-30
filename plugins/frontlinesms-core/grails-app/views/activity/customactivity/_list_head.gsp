<ul class="info">
	<h1>
		<g:message code="customactivity.title" args="${[ownerInstance.name]}"/>
	</h1>
	<li>
		<g:formatDate date="${ownerInstance?.dateCreated}"/>
	</li>
	<li>
	<g:if test="${ownerInstance?.keywords}">
		<h2 id="web_connection_keywords"><g:message code="poll.keywords"/> : ${ownerInstance?.keywords*.value.join(',')}</h2>
	</g:if>
	</li>
	<li>
		<g:select name="toggleStep" noSelection="${['null': 'Overview']}"
			from="${ownerInstance?.steps}" value="${stepInstance?.id}"
			optionKey="id" optionValue="niceFormat">
		</g:select>
	</li>
</ul>

<div>
	<fsms:render template="/activity/customactivity/step_summary" model="${[steps:ownerInstance?.steps]}"/>
</div>
