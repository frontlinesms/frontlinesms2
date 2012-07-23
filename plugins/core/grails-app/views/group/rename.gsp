<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<div class="error-panel hide" id="smallpopup-error-panel"><div id="error-icon"></div></div>
	<g:formRemote name="group-details" url="[controller: 'group', action:'update']" method="post" onSuccess="checkResults(data)">
		<g:hiddenField name="id" value="${params.groupId}"></g:hiddenField>
		<div class="dialog">
			<table>
				<tbody>
					<tr class="prop">
						<td valign="top" class="name">
							<label class="bold inline" for="name"><g:message code="group.name.label" default="Name"/></label>
						</td>
						<td valign="top" class="value">
							<g:textField name="name" value="${params.groupName}"/>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</g:formRemote>
</div>