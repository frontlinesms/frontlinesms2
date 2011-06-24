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
                                    <label for="name"><g:message code="folder.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="name ${hasErrors(bean: folderInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${folderInstance?.name}" />
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
