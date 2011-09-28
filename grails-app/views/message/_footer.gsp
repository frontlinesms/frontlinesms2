<div class="content-footer">
	<ul id="filter">
		<li>Show:</li>
		<li><g:link action="${messageSection}" params="${params.findAll({it.key != 'starred' && it.key != 'failed' && it.key != 'max' && it.key != 'offset'})}">All</g:link></li>
		<li>|</li>
		<li>
		<g:if test="${messageSection == 'pending'}">
			<g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset'}) + [failed: true]}" >Failed</g:link>
		</g:if>
		<g:else>
			<g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset'}) + [starred: true]}" >Starred</g:link>
		</g:else>
		</li>
	</ul>
	<div id="page-arrows">
		<g:paginate next="Next" prev="Back"
			max="${grailsApplication.config.grails.views.pagination.max}"
			action="${messageSection}" total="${messageInstanceTotal}" params="${params.findAll({it.key != 'messageId'})}"/>
	</div>
</div>