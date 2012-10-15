<%@ page import="frontlinesms2.Subscription" %>
<h2><g:message code="subscription.top.keyword.header"/></h2>
<div class="info">
	<p><g:message code="subscription.top.keyword.header"/></p>
</div>
<div class="input">
	<g:textField name="keywords" value="${activityInstanceToEdit?.keyword?.value}" class="required"/>
</div>

<h2><g:message code="subscription.keywords.header"/></h2>
<div class="info">
	<p><g:message code="subscription.keywords.description"/></p>
</div>
<div class="input">
	<table class="subscription-aliases">
		<tr>
			<td><label for="joinKeywords"><g:message code="subscription.keywords.join"/></label></td>
			<td><g:textField class="aliases validcommas" name="joinKeywords" id="joinKeywords" value="${activityInstanceToEdit?.joinKeywords}"/></td>
		</tr>
		<tr>
			<td><label for="leaveKeywords"><g:message code="subscription.keywords.leave"/></label></td>
			<td><g:textField class="aliases validcommas" name="leaveKeywords" id="leaveKeywords" value="${activityInstanceToEdit?.leaveKeywords}"/></td>
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


