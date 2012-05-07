<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form action="delete" method="post" >
		<g:hiddenField name="id" value="${params.groupId}"></g:hiddenField>
		<div class="dialog">
			<p><g:message code="group.delete.prompt" args="${ [groupName] }"/></p>
		</div>
	</g:form>
</div>