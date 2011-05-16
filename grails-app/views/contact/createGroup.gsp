<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="contacts" />
		<title>Create Group</title>
	</head>
	<body>
		<h1><g:message code="default.create.label" args="[entityName]" /></h1>
		<g:form name="groupDetails" action="saveGroup" >
			<div class="dialog">
				<table>
					<tbody>
						<tr class="prop">
							<td valign="top" class="name">
								<label for="name"><g:message code="group.name.label" default="Name" /></label>
							</td>
							<td valign="top" class="value ${hasErrors(bean: groupInstance, field: 'name', 'errors')}">
								<g:textField name="name" value="${groupInstance?.name}" />
							</td>
						</tr>

					</tbody>
				</table>
			</div>


			<div id="buttons">
				<g:submitButton name="createGroup" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
				<g:link class="cancel" action="list">Cancel</g:link>
			</div>
		</g:form>
  	</body>
</html>