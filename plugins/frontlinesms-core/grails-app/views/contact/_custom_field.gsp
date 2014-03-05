<tr class="input custom-field editable ${selected? 'selected': ''}">
	<td><label for="${fieldName}">${name}</label></td>
	<td>
		<g:textField name="${fieldName}" value="${value}" lastsavedvalue="${value}"/>
		<a id="${removerName}" class="icon-remove remove-command"></a>
	</td>
</tr>

