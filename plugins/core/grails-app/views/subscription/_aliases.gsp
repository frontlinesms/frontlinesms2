<h2><g:message code="subscription.aliases.header"/></h2>
<div class="input">
	<table class="subscription-aliases">
		<tr>
			<td><label for="join-aliases"><g:message code="subscription.aliases.join"/></label></td>
			<td><g:textField name="join-aliases" id="join-aliases" value="${activityInstanceToEdit?.leaveAliases?.value}"/></td>
		</tr>
		<tr>
			<td><label for="leave-aliases"><g:message code="subscription.aliases.leave"/></label></td>
			<td><g:textField name="leave-aliases" id="leave-aliases" value="${activityInstanceToEdit?.leaveAliases?.value}"/></td>
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
			<g:radio name="defaultAction" value="join" checked="${activityInstanceToEdit?.defaultAction? activityInstanceToEdit?.defaultAction == 'join' : true}"/>
		</li>
		<li>
			<label for="defaultAction"><g:message code="subscription.default.action.leave"/></label>
			<g:radio name="defaultAction" value="join" checked="${activityInstanceToEdit?.defaultAction? activityInstanceToEdit?.defaultAction == 'leave' : false}"/>
		</li>
		<li>
			<label for="defaultAction"><g:message code="subscription.default.action.toggle"/></label>
			<g:radio name="defaultAction" value="join" checked="${activityInstanceToEdit?.defaultAction? activityInstanceToEdit?.defaultAction == 'toggle' : false}"/>
		</li>
	</ul>
</div>

<r:script>
	$(function () {	$(".dropdown").selectmenu(); })
</r:script>

