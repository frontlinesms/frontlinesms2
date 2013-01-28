<%@ page import="frontlinesms2.Webconnection" %>
<g:if test="${!activityInstanceToEdit?.id}">
	<fsms:radioGroup title="webconnection.select.type"
			solo="true" checked="generic"
			name="webconnectionType"
			values="${Webconnection.implementations*.type}"
			labelPrefix="webconnection." labelSuffix=".label"
			descriptionPrefix="webconnection." descriptionSuffix=".description"
			onchange="setType(this.value)"/>
</g:if>

