<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="frontlinesms2.Subscription" %>
<meta name="layout" content="popup"/>
<div>
	<g:form name="categorize_subscription" controller="subscription">
		<g:hiddenField id="messages" name="messagesList" value="${params.messageId}"/>
		<g:hiddenField id="owner" name="ownerId" value="${params.ownerId}"/>
		<p class="info">${Subscription.get(params.ownerId)?.name}</p>
		<g:actionSubmit id="btn_join" class="msg-btn btn" value="${g.message(code:'subscription.join')}" action="join"/>
		<g:actionSubmit id="btn_leave" class="msg-btn btn" value="${g.message(code:'subscription.leave')}" action="leave"/>
	</g:form>
</div>