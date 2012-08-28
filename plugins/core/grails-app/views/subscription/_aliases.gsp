<%@ page import="frontlinesms2.Subscription" %>
<h2><g:message code="subscription.aliases.header"/></h2>
<div class="input">
	<table class="subscription-aliases">
		<tr>
			<td><label for="joinAliases"><g:message code="subscription.aliases.join"/></label></td>
			<td><g:textField class="aliases validcommas" name="joinAliases" id="joinAliases" value="${activityInstanceToEdit?.joinAliases}"/></td>
		</tr>
		<tr>
			<td><label for="leaveAliases"><g:message code="subscription.aliases.leave"/></label></td>
			<td><g:textField class="aliases validcommas" name="leaveAliases" id="leaveAliases" value="${activityInstanceToEdit?.leaveAliases}"/></td>
		</tr>
	</table>
</div>

<h2><g:message code="subscription.default.action.header"/></h2>
<div class="info">
	<p><g:message code="subscription.default.action.description"/></p>
</div>
<div class="input">
	<ul class="select">
		<li>
			<label for="defaultAction"><g:message code="subscription.default.action.join"/></label>
			<g:radio name="defaultAction" value="join" checked="${activityInstanceToEdit?.defaultAction? activityInstanceToEdit?.defaultAction == Subscription.Action.JOIN : true}"/>
		</li>
		<li>
			<label for="defaultAction"><g:message code="subscription.default.action.leave"/></label>
			<g:radio name="defaultAction" value="leave" checked="${activityInstanceToEdit?.defaultAction? activityInstanceToEdit?.defaultAction == Subscription.Action.LEAVE : false}"/>
		</li>
		<li>
			<label for="defaultAction"><g:message code="subscription.default.action.toggle"/></label>
			<g:radio name="defaultAction" value="toggle" checked="${activityInstanceToEdit?.defaultAction? activityInstanceToEdit?.defaultAction == Subscription.Action.TOGGLE : false}"/>
		</li>
	</ul>
</div>


