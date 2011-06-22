<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Create Custom Field</title>
  </head>
  <body>
   <g:form name="field-details" action="saveCustomField" >
	  <div class="dialog">
		  <table>
			  <tbody>
				  <tr class="prop">
					  <td valign="top" class="title">
						  <label for="name"><g:message code="customfield.name.label" default="Name" /></label>
					  </td>
					  <td valign="top" class="value ${hasErrors(bean: customFieldInstance, field: 'name', 'errors')}">
						  <g:textField name="name" value="${customFieldInstance.name}" />
					  </td>
				  </tr>
				  <tr class="prop">
					  <td valign="top" class="value">
						  <label for="responses"><g:message code="customfield.value.label" default="Value" /></label>
					  </td>
					  <td valign="top" class="value ${hasErrors(bean: customFieldInstance, field: 'value', 'errors')}">
						  <g:textField name="value" value="${customFieldInstance.value}" />
					  </td>
				  </tr>
			  </tbody>
		  </table>
	  </div>
	  <div class="buttons">
		  <g:submitButton name="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
		  <g:link class="cancel" controller="contact" action="show">Cancel</g:link>
	  </div>
	</g:form>
  </body>
</html>
