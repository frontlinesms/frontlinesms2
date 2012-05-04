<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form action="save" method="post" >
		<g:hiddenField name="id" value="${params.groupId}"></g:hiddenField>
		<div class="dialog">
			<table>
				<tbody>
					<tr class="prop">
						<td valign="top" class="name">
							<label class="bold inline" for="name"><g:message code="smartgroup.name.label"/></label>
						</td>
						<td valign="top" class="value">
							<g:textField name="smartgroupname"/>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</g:form>
</div>
