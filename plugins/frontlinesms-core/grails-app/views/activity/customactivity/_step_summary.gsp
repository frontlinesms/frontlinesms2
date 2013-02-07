<%@ page import="frontlinesms2.JoinActionStep" %>
<%@ page import="frontlinesms2.LeaveActionStep" %>
<%@ page import="frontlinesms2.ReplyActionStep" %>

<g:if test="${stepInstance?.shortName == 'join'}">
	<div class='step-summary ' id="step-${stepInstance?.id}">
		<p>${stepInstance?.group.name}</p>
		<p>${stepInstance?.group.members.size()} members</p>
	</div>
</g:if>
<g:if test="${stepInstance?.shortName == 'leave'}">
	<div class='step-summary ' id="step-${stepInstance?.id}">
		<p>${stepInstance?.group.name}</p>
		<p>${step.groupInstance?.members.size()} members</p>
	</div>
</g:if>
<g:if test="${stepInstance?.shortName == 'reply'}">
	<div class='step-summary ' id="step-${stepInstance?.id}">
		<p>${stepInstance?.getProperty('autoreplyText')}</p>
	</div>
</g:if>
