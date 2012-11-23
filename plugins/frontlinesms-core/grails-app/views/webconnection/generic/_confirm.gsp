<%@ page import="frontlinesms2.GenericWebconnection" %>
<div class="input">
	<label for="name"><g:message code="webconnection.name.prompt"/></label>
	<g:textField name="name" value="${activityInstanceToEdit?.name}" class="required"/>
</div>
<div class="confirm">
	<h2><g:message code="webconnection.details.label"/></h2>
	<fsms:activityConfirmTable fields="httpMethod, url, keyword, parameters" type="${GenericWebconnection.type}" instanceClass="${GenericWebconnection}"/>
</div>
<div class="input">
	<label for="testConnection"><g:message code="webconnection.test.prompt"/>
		<g:checkBox name="testConnection" value="" onclick="toggleTestButton(this);"/>
	</label>
</div>

