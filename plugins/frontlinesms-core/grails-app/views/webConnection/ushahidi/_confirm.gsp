<%@ page import="frontlinesms2.UshahidiWebConnection" %>
<div class="input">
	<label for="name"><g:message code="webconnection.name.prompt"/></label>
	<g:textField name="name" value="${activityInstanceToEdit?.name}" class="required"/>
</div>
<div class="confirm">
	<h2><g:message code="webconnection.details.label"/></h2>
	<fsms:activityConfirmTable fields="service, url, key, keyword" type="${UshahidiWebConnection.type}" instanceClass="${UshahidiWebConnection}"/>
</div>

