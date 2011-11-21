<div class="secondary-section-actions">
	<form method="get">
		<input id="contact-search" class="default-text-input" type="text" onkeyup="${remoteFunction(action:'search', onSuccess: 'updateContacts(data)', params:'\'searchString=\' + this.value + \'&groupId=\'+getGroupId()')}" value="${params.searchString?:'Search'}" defaultValue='Search' />
	</form>
	<div id="page-arrows">
		<g:if test="${contactsSection instanceof frontlinesms2.Group}">
			<g:set var="parameters" value="${[searchString: params.searchString, groupId: contactsSection.id]}" />
		</g:if>
		<g:else>
			<g:set var="parameters" value="${[searchString:params.searchString]}" />
		</g:else>
		<g:paginate next="Next" prev="Back"  action="show" total="${contactInstanceTotal ?: 0}" params="${parameters}"
			max="${grailsApplication.config.grails.views.pagination.max}" />
	</div>
</div>
