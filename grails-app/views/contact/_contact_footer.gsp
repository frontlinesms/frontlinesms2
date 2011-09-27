<div class="content-footer">
	<input id="contact-search" class="default-text-input" type="text" onkeyup="${remoteFunction(action:'search', onSuccess: 'updateContacts(data)', params:'\'searchString=\' + this.value + getGroupId()')}" value="${params.searchString?:'Search'}" defaultValue='Search' />
	<div id="page-arrows">
		<g:if test="${contactsSection instanceof frontlinesms2.Group}">
			<g:set var="parameters" value="${[searchString: params.searchString, groupId: contactsSection.id]}" />
		</g:if>
		<g:else>
			<g:set var="parameters" value="${[searchString:params.searchString]}" />
		</g:else>
		<g:paginate next="Next" prev="Back"  action="list" total="${contactInstanceTotal}" params="${parameters}"
			max="${grailsApplication.config.grails.views.pagination.max}" />
	</div>
</div>