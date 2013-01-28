<%@ page import="frontlinesms2.JoinActionStep" %>
<%@ page import="frontlinesms2.LeaveActionStep" %>
<%@ page import="frontlinesms2.ReplyActionStep" %>

<g:each var="step" in="${steps}">
	<g:if test="${step.shortName == 'join'}">
		<div class='step-summary hidden' id="step-${step.id}">
			<p>${step.group.name}</p>
			<p>${step.group.members.size()} members</p>
		</div>
	</g:if>
	<g:if test="${step.shortName == 'leave'}">
		<div class='step-summary hidden' id="step-${step.id}">
			<p>${step.group.name}</p>
			<p>${step.group.members.size()} members</p>
		</div>
	</g:if>
	<g:if test="${step.shortName == 'reply'}">
		<div class='step-summary hidden' id="step-${step.id}">
			<p>${step.getProperty('autoreplyText')}</p>
		</div>
	</g:if>
</g:each>
<style>
	.hidden{
		display: none;
	}
</style>
<r:script>
	$("#toggleStep").on("change", function(){
		var stepID = $(this).val();
		$(".step-summary").addClass("hidden");
		$("#step-"+stepID).removeClass("hidden");
	});
</r:script>
