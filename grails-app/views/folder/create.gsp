<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
	<meta name="layout" content="folders" />
    <title>Folder</title>
  </head>
  <body>
   <g:form name="folder-details" action="save" >
                <div class="dialog">
                    <table>
                        <tbody>
                            <tr class="prop">
                                <td valign="top" class="title">
                                    <label for="value"><g:message code="folder.value.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: folderInstance, field: 'value', 'errors')}">
                                    <g:textField name="value" value="${folderInstance?.value}" />
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
  </body>
</html>
