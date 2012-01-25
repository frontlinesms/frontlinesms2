<ul id="radio-actions">
	<li>
		<g:remoteLink controller="radioShow" action="startShow" id="${ownerInstance?.id}" onSuccess="startShow(data)" class="btn start-show" disabled="${ownerInstance?.isRunning ? 'disabled' : ''}">
				Start Show
		</g:remoteLink>
	</li>
	<li>
		<g:remoteLink controller="radioShow" action="stopShow" id="${ownerInstance?.id}" onSuccess="stopShow(data)" class="btn stop-show" disabled="${ownerInstance?.isRunning ? '' : 'disabled'}">
				Stop Show
		</g:remoteLink>
	</li>
</ul>