<div class="footer message">
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
	<div id="paging">
		<g:paginate next="Next" prev="Back"
			max="${grailsApplication.config.grails.views.pagination.max}"
			action="${messageSection}" total="${messageInstanceTotal}" params="${params.findAll({it.key != 'messageId'})}"/>
	</div>
</div>
