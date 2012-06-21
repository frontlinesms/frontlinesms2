<ul class="info">
	<li>
		<h1>
			<g:message code="folder.title" args="${[ownerInstance.name]}"/>
			<span id="on-air" class="${ownerInstance?.isRunning ? 'onAirIsActive' : ''}"><g:message code="radio.show.onair" /></span>
		</h1>
	</li>
	<li>
		<g:remoteLink controller="radioShow" action="startShow" id="${ownerInstance?.id}" onSuccess="startShow(data)" class="btn start-show btn" disabled="${ownerInstance?.isRunning ? 'disabled' : ''}">
				<g:message code="radio.show.start.show" />
		</g:remoteLink>
	</li>
	<li>
		<g:remoteLink controller="radioShow" action="stopShow" id="${ownerInstance?.id}" onSuccess="stopShow(data)" class="btn stop-show" disabled="${ownerInstance?.isRunning ? '' : 'disabled'}">
				<g:message code="radio.show.stop.show" />
		</g:remoteLink>
	</li>
</ul>

