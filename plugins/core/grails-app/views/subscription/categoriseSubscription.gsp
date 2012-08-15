<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="frontlinesms2.Subscription" %>
<meta name="layout" content="popup"/>
<div>
	<g:form name="categorize_subscription" controller="message" action="categoriseIntoSubscriptions">
		<g:hiddenField name="messagesList" value="${params.messageId}"/>
		<g:hiddenField name="ownerId" value="${params.ownerId}"/>
		<p>${Subscription.get(params.ownerId)?.name}</p>
		<g:link name='btn_join' class="btn" onClick='categoriseSubscription()' ><g:message code="subscription.join"/></g:link>
		<g:link name='btn_leave' class="btn" controller='subscription' action='categoriseSubscription'><g:message code="subscription.leave"/></g:link>
	</g:form>
</div>
