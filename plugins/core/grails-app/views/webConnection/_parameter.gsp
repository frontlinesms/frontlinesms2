<%@ page import="frontlinesms2.WebConnection" %>
<tr class="prop web-connection-parameter">
	<td>
		<g:textField name="param-name" class="param-name" value='${name}' required="true"/>
	</td>
	<td>
		<g:textField name="param-value" class="param-value" value='${value}'/>
	</td>
	<td>
		<fsms:magicWand target="param-value" fields="${WebConnection.subFields*.key}" onchange='autofillValue(this);'/>
	</td>
	<td>
		<a onclick="removeRule(this)" class="remove-command" style="display:${isFirst?'none':'auto'}"/>
	</td>
</tr>

