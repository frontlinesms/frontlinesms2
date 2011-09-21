<g:form name="message-action" controller="message" method="POST">
	<g:hiddenField name="messageSection" value="${messageSection}"></g:hiddenField>
	<g:hiddenField name="ownerId" value="${ownerInstance?.id}"></g:hiddenField>
	<g:hiddenField name="messageId" value="${messageInstance.id}"></g:hiddenField>
	<g:hiddenField name="checkedMessageList" value="${params.checkedMessageList}"></g:hiddenField>
	<g:hiddenField name="viewingArchive" value="${params.viewingArchive}"></g:hiddenField>
	<g:if test="${messageSection == 'search'}">
		<g:hiddenField name="searchId" value="${search.id}"></g:hiddenField>
	</g:if>
		<li class='static_btn'>
			<g:actionSubmit value="${value}" id="${id}" action="${action}"/>
		</li>
</g:form>
