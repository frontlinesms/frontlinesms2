<ul class="info">
	<h1>
		<g:message code="${ownerInstance.shortName}.title" args="${[ownerInstance.name]}"/>
	</h1>
	<li>
		<g:formatDate date="${ownerInstance.dateCreated}"/>
	</li>
	<li>
		${ownerInstance.autoreplyText}
	</li>
</ul>

