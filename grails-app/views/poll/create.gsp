<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="poll-details" action="save" >
		<div class="dialog">
			<table>
				<tbody>
					<tr class="prop">
						<td valign="top" class="title">
							<label for="title"><g:message code="poll.title.label" default="Title" /></label>
						</td>
						<td valign="top" class="value ${hasErrors(bean: pollInstance, field: 'title', 'errors')}">
							<g:textField name="title" value="${pollInstance?.title}" />
						</td>
					</tr>
					<tr class="prop">
						<td valign="top" class="responses">
							<label for="responses"><g:message code="responses.value.label" default="Responses" /></label>
						</td>
						<td valign="top" class="value ${hasErrors(bean: pollInstance, field: 'responses', 'errors')}">
							<g:textField name="responses" value="${responses}" />
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="buttons">
			<g:submitButton name="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
			<g:link class="cancel" controller="message" action="inbox">Cancel</g:link>
		</div>
	</g:form>
</div>
