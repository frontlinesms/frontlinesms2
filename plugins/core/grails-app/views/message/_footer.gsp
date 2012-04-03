<div class="footer message">
	<g:if test="${messageInstanceTotal > 0}">
		<div id="message-toggler">
			<g:message code="footer.show.label" />:
			<g:link action="${messageSection}" params="${params.findAll({it.key != 'starred' && it.key != 'failed' && it.key != 'max' && it.key != 'offset'})}"><g:message code="footer.show.all" /></g:link>
			|
			<g:if test="${messageSection == 'pending'}">
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset'}) + [failed: true]}" ><g:message code="footer.show.failed" /></g:link>
			</g:if>
			<g:else>
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset'}) + [starred: true]}" ><g:message code="footer.show.starred" /></g:link>
			</g:else>
		</div>
		<div id="paging">
			<g:paginate next="Next" prev="Back"
				max="${grailsApplication.config.grails.views.pagination.max}"
				action="${messageSection}" total="${messageInstanceTotal ?: itemInstanceTotal}" params="${params.findAll({it.key != 'messageId'})}"/>
		</div>
	</g:if>
</div>
