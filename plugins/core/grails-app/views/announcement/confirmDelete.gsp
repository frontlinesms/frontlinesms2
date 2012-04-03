<div>
	<g:form name="confirmDelete" action='delete' >
		<g:hiddenField name="id" value="${params.id}"/>
			<h2><g:message code="announcement.delete.warn" args="${ [ownerInstance.name] }?" />
	</g:form>
</div>