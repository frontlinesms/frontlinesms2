<div class="footer message">
	<g:if test="${messageInstanceTotal > 0}">
		<div id="message-toggler">
			Show:
			<g:link action="${messageSection}" params="${params.findAll({it.key != 'starred' && it.key != 'failed' && it.key != 'max' && it.key != 'offset'})}">All</g:link>
			|
			<g:if test="${messageSection == 'pending'}">
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset'}) + [failed: true]}" >Failed</g:link>
			</g:if>
			<g:else>
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset'}) + [starred: true]}" >Starred</g:link>
			</g:else>
		</div>
		<div id="paging">${messageInstance?.hasPending} ${messageInstance?.hasFailed} ${messageInstance?.hasSent} ${messageInstance?.dispatches} ${messageInstance?.dispatches.status}
			<g:paginate next="Next" prev="Back"
				max="${grailsApplication.config.grails.views.pagination.max}"
				action="${messageSection}" total="${messageInstanceTotal ?: itemInstanceTotal}" params="${params.findAll({it.key != 'messageId'})}"/>
		</div>
	</g:if>
</div>
