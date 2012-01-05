<div id="radio-actions">
	<g:remoteLink controller="radioShow" action="startShow" id="${ownerInstance?.id}" onSuccess="startShow(data)" class="section-action-button btn start-show" disabled="${ownerInstance?.isRunning ? 'disabled' : ''}">
			Start Show
	</g:remoteLink>
	<g:remoteLink controller="radioShow" action="stopShow" id="${ownerInstance?.id}" onSuccess="stopShow(data)" class="section-action-button btn stop-show" disabled="${ownerInstance?.isRunning ? '' : 'disabled'}">
			Stop Show
	</g:remoteLink>
</div>