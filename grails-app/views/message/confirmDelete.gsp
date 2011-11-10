<div>
	<g:form name="confirmDelete" action='delete' >
		<g:hiddenField name="id" value="${params.id}"/>
			<h2>Move '${ownerInstance instanceof frontlinesms2.Poll ? ownerInstance.title : ownerInstance.name}' to trash. This will transfer all associated messages to the trash section.</h2>
	</g:form>
</div>