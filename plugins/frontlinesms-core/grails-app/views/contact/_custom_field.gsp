<tr class="input custom-field editable ${selected? 'selected': ''}">
	<td><label for="${fieldName}">${name}</label></td>
	<td>
		<g:textField name="${fieldName}" value="${value}" lastsavedvalue="${value}"/>
		<a id="${removerName}" class="fa fa-times remove-command"></a>
	</td>
</tr>

