<%@ page import="frontlinesms2.*" %>
<g:if test="${!fconnectionInstance}">
	<fsms:radioGroup solo="true" title="connection.select"
			name="connectionType" checked="smslib"
			values="${Fconnection.getImplementations(params)*.shortName}"
			labelSuffix=".label"
			descriptionSuffix=".description"
			onclick="fconnection.setType(this.value)"/>
</g:if>
<g:else>
	<g:hiddenField name="connectionType" value="${fconnectionInstance?.shortName}"/>
</g:else>

