<%@ page contentType="text/html;charset=UTF-8" %>
<g:form name="folder-details" action="update">
	<table>
		<fsms:input instance="${folderInstance}" field="name" fieldPrefix="" table="true"/>
		<input name="id" value="${folderInstance.id}" type="hidden"/>
	</table>
</g:form>