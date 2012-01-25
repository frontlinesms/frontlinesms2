<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form action="delete" method="post" >
		<g:hiddenField name="id" value="${params.groupId}"></g:hiddenField>
		<div class="dialog">
			<p>Are you sure you want to delete ${groupName}? WARNING: This cannot be undone</p>
		</div>
	</g:form>
</div>