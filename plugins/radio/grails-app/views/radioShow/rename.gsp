<%@ page contentType="text/html;charset=UTF-8" %>
<g:form name="radioShow-details" action="save">
	<radioShow>
		<fsms:input instance="${showInstance}" field="name" fieldPrefix="" table="true"/>
		<input name="ownerId" value="${showInstance.id}" type="hidden"/>
	</table>
</g:form>