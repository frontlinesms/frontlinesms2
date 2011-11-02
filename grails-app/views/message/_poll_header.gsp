<div>
	<g:if test="${params.viewingArchive && params.viewingMessages}">
		<g:link controller="archive" action="poll"> &lt; Back </g:link>
	</g:if>
	<g:else>
		<img src='${resource(dir:'images/icons',file:'activities.png')}' />
	</g:else>
	<g:render template="../poll/poll_details" />
</div>
