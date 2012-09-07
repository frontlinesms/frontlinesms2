<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="frontlinesms2.Subscription" %>
<meta name="layout" content="popup"/>
<div>
	<g:form name="categorize_subscription" controller="subscription">
		<g:hiddenField id="messages" name="messagesList" value="${params.messageId}"/>
		<g:hiddenField id="owner" name="ownerId" value="${params.ownerId}"/>
		<p class="info"><g:message code="subscription.categorise.info" args="${[Subscription.get(params.ownerId)?.name]}" /></p>
		<div class="subscription_input">
			<g:radio name="subscription-action" value="join" checked="checked"/>
			<g:message code="subscription.categorise.join.label" args="${[Subscription.get(params.ownerId)?.group?.name]}"/>
		</div>
		<div class="subscription_input">
			<g:radio name="subscription-action" value="leave"/>
			<g:message code="subscription.categorise.leave.label" args="${[Subscription.get(params.ownerId)?.group?.name]}"/>
		</div>
		<div class="subscription_input">
			<g:radio name="subscription-action" value="toggle"/>
			<g:message code="subscription.categorise.toggle.label" args="${[Subscription.get(params.ownerId)?.group?.name]}"/>
		</div>
		<!--
		<g:actionSubmit id="btn_join" class="msg-btn btn" value="${g.message(code:'subscription.join')}" action="join"/>
		<g:actionSubmit id="btn_leave" class="msg-btn btn" value="${g.message(code:'subscription.leave')}" action="leave"/>
		-->
	</g:form>
</div>