<%@ page import="frontlinesms2.UshahidiWebConnection" %>
<div class="input">
	<label for="name"><g:message code="webConnection.name.prompt"/></label>
	<g:textField name="name" value="${activityInstanceToEdit?.name}" class="required"/>
</div>
<div class="confirm">
	<h2><g:message code="webConnection.details.label"/></h2>
	<fsms:activityConfirmTable fields="service, url, key, keyword" type="${UshahidiWebConnection.type}" instanceClass="${activityInstanceToEdit?.class}"/>
</div>

