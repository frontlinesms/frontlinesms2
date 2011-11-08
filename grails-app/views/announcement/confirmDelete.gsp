<div>
	<g:form name="confirmDelete" action='delete' >
		<g:hiddenField name="id" value="${params.id}"/>
			<h2>Delete ${announcementName}? WARNING: This cannot be undone!</h2>
	</g:form>
</div>