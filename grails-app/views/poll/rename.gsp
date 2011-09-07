<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="group-details" action="update" >
		<g:hiddenField name="id" value="${params.ownerId}"></g:hiddenField>
		<div class="dialog">
			<table>
				<tbody>
					<tr class="prop">
						<td valign="top" class="name">
							<label for="title">Name</label>
						</td>
						<td valign="top" class="value">
							<g:textField name="title" />
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</g:form>
</div>