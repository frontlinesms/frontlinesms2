<div class="controls">
	<g:if test="${messageSection}">
		<div id="message-toggler">
			<g:message code="fmessage.footer.show"/>:
			<g:link action="${messageSection}" params="${params.findAll({it.key != 'starred' && it.key != 'inbound' && it.key != 'failed' && it.key != 'max' && it.key != 'offset'})}" class="${!params.starred && (params.inbound == null) ? 'active' : ''}"><g:message code="fmessage.footer.show.all"/></g:link>
			|
			<g:if test="${messageSection == 'pending'}">
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset'}) + [failed: true]}" ><g:message code="fmessage.footer.show.failed"/></g:link>
			</g:if>
			<g:else>
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset'}) + [starred: true]}" class="${params.starred ? 'active' : ''}"><g:message code="fmessage.footer.show.starred"/></g:link>
			</g:else>
			<g:if test="${messageSection == 'folder' || messageSection == 'activity'}">
				|
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset' && it.key != 'starred'}) + [inbound: true]}" class="${params.inbound == 'true' ? 'active' : ''}"><g:message code="fmessage.footer.show.incoming"/></g:link>
				|
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset' && it.key != 'starred'}) + [inbound: false]}" class="${params.inbound == 'false' ? 'active' : ''}"><g:message code="fmessage.footer.show.outgoing"/></g:link>
			</g:if>
		</div>
		<div id="paging">
			<g:hiddenField name="offset" value="${params.offset}" />
			<g:if test="${messageInstanceTotal > 0}">
				<g:paginate next="Next" prev="Back"
					max="${grailsApplication.config.grails.views.pagination.max}"
					action="${messageSection}" total="${messageInstanceTotal ?: itemInstanceTotal}" params="${params.findAll({it.key != 'messageId'})}"/>
			</g:if>
		</div>
	</g:if>
</div>
