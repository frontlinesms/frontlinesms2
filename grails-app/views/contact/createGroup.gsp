<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="contacts" />
	</head>
	<body>
		<g:form name="groupDetails" action="save" >
			<div class="dialog">
				<table>
					<tbody>

						<tr class="prop">
							<td valign="top" class="name">
								<label for="address"><g:message code="contact.address.label" default="Address" /></label>
							</td>
							<td valign="top" class="value ${hasErrors(bean: contactInstance, field: 'address', 'errors')}">
								<g:textField name="address" value="${contactInstance?.address}" />
							</td>
						</tr>

						<tr class="prop">
							<td valign="top" class="name">
								<label for="name"><g:message code="contact.name.label" default="Name" /></label>
							</td>
							<td valign="top" class="value ${hasErrors(bean: contactInstance, field: 'name', 'errors')}">
								<g:textField name="name" value="${contactInstance?.name}" />
							</td>
						</tr>

					</tbody>
				</table>
			</div>


			<div id="buttons">
				<g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
				<g:link class="cancel" action="list">Cancel</g:link>
			</div>
		</g:form>
  	</body>
</html>