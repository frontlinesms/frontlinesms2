<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="folder-details" action="save" >
		<label for="name">Name</label>
		<g:textField name="name" value="${folderInstance?.name}" />
	</g:form>
</div>
