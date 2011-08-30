<div>
	<g:form name="confirmDelete" action="deleteContact" >
		<g:hiddenField name="checkedContactList" value="${params?.checkedContactList?}"/>
		<h2>${params?.message?}</h2>
	</g:form>
</div>