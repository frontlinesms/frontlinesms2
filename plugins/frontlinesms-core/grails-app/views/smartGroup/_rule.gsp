<tr class="prop smart-group-criteria">
	<td>
		<g:select name="rule-field"
				value="${key}"
				from="${fieldNames}"
				keys="${fieldIds}"
				onchange="smartGroupCriteriaChanged(this)"/>
	</td>
	<td class="rule-match-text">
		<span class="contains ${key=='mobile'?'hide':''}"><g:message code="smartgroup.contains.label"/></span>
		<span class="starts ${key!='mobile'?'hide':''}"><g:message code="smartgroup.startswith.label"/></span>
	</td>
	<td>

		<g:textField name="rule-text" class="rule-text" value='${value}'/>
	</td>
	<td>
		<a onclick="removeRule(this)" class="remove-command fa fa-times" style="display:${isFirst?'none':'auto'}"/>
	</td>
</tr>

