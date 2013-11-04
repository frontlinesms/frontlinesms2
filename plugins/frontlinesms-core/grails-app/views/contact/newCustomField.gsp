<%@ page contentType="text/html;charset=UTF-8" %>
<div class="error-panel hide" id="smallpopup-error-panel"><div id="error-icon"></div></div>
<g:formRemote name="custom-field-popup" url="[controller: 'contact', action:'getUniqueCustomFields']" method="post" onSuccess="contactEditor.checkCustomFieldResult(data)">
	<table>
		<tr>
			<td><label for="name"><g:message code="customfield.name.label"/></label></td>
			<td><g:textField id="custom-field-name" name="custom-field-name" value=""/></td>
		</tr>
	</table>
</g:formRemote>

