<%@ page import="frontlinesms2.WebConnection" %>
<tr class="prop web-connection-parameter">
	<td>
		<label for="param-name"><g:message code="webConnection.param.name"/></label>
		<g:textField name="param-name" class="param-name" value='${name}' required="true"/>
	</td>
	<td>
		<label for="param-value"><g:message code="webConnection.param.value"/></label>
		<g:textField name="param-value" class="param-value" value='${value}'/>
	</td>
	<td>
		<fsms:magicWand target="param-value" fields="${WebConnection.subFields}" onchange='autofillValue(this);'/>
	</td>
	<td>
		<a onclick="removeRule(this)" class="remove-command" style="display:${isFirst?'none':'auto'}"/>
	</td>
</tr>

