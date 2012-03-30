<div>
	<g:form name="confirmDelete" action='delete' >
		<g:hiddenField name="id" value="${params.id}"/>
			<h2><g:message code="announcement.delete.prompt" args="${ [ownerInstance.name] }?" />
	</g:form>
</div>