<ul class="info">
	<li>
		<h1>
			<g:message code="folder.title" args="${[ownerInstance.name]}"/>
			<span id="on-air" class="${ownerInstance?.isRunning ? 'onAirIsActive' : ''}"><g:message code="radio.show.onair" /></span>
		</h1>
	</li>
	<li>
		<g:if test="${!ownerInstance.archived && !ownerInstance.deleted}">
			<g:if test="${ownerInstance.isRunning}">
				<g:link controller="radioShow" action="stopShow" id="${ownerInstance?.id}" onSuccess="stopShow(data)" class="btn stop-show">
					<g:message code="radio.show.stop.show" />
				</g:link>
			</g:if>
			<g:else>
				<g:link controller="radioShow" action="startShow" id="${ownerInstance?.id}" onSuccess="startShow(data)" class="btn start-show btn">
					<g:message code="radio.show.start.show" />
				</g:link>
			</g:else>
		</g:if>
	</li>
</ul>

