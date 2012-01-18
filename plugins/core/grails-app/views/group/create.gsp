<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="group-details" action="save" >
		<div class="dialog">
			<table>
				<tbody>
					<tr class="prop">
						<td valign="top" class="name">
							<label class="bold inline" for="name"><g:message code="group.name.label" default="Name" />:</label>
						</td>
						<td valign="top" class="value ${hasErrors(bean: groupInstance, field: 'name', 'errors')}">
							<g:textField name="name" value="${groupInstance?.name}" />
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</g:form>
</div>