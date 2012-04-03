<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="folder-details" action="save" >
		<label class="bold inline" for="name"><g:message code="folder.name.label" /></label>
		<g:textField name="name" value="${folderInstance?.name}" />
	</g:form>
</div>
