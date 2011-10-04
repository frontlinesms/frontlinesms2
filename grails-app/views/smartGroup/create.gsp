<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="smart-group-details" action="save" >
		<div class="dialog">
			<table>
				<tbody>
					<tr class="prop">
						<td valign="top" class="name">
							<label for="name"><g:message code="smartgroup.name.label" default="Name" /></label>
						</td>
						<td valign="top" class="value ${hasErrors(bean: smartGroupInstance, field: 'name', 'errors')}">
							<g:textField name="name" value="${smartGroupInstance?.name}" />
						</td>
						<td></td>
						<td></td>
					</tr>
					<tr class="prop smartgroup-criteria" id="smartgroup-rule-template">
						<td>
							<g:select name="rule.field"
									from="['Contact name', 'Phone number', 'email', 'notes']"/>
						</td>
						<td>
							<span class='contains'>contains</span>
							<span class='starts'>starts with</span>
						</td>
						<td>
							<g:textField name="rule"/>
						</td>
						<td>
						</td>
					</tr>
				</tbody>
			</table>
			<a class="button">Add another rule</a>
		</div>
	</g:form>
</div>
