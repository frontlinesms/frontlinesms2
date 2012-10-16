<%@ page import="frontlinesms2.Subscription" %>
<h2><g:message code="subscription.sorting"/></h2>
<div class="input">
	<ul class="select">
		<li>
			<g:checkBox name="disableSorting" checked="activityInstanceToEdit ? !activityInstanceToEdit?.keywords : false"/>
			<label for="leaveKeywords"><g:message code="subscription.sorting.disable"/></label>
		</li>
	</ul>
</div>
<h2><g:message code="subscription.top.keyword.header"/></h2>
<div class="info">
	<p><g:message code="subscription.top.keyword.header"/></p>
</div>

<div class="input">
	<g:textField name="topLevelKeywords" value="${activityInstanceToEdit?.keywords?.findAll { it.isTopLevel && !it.ownerDetail}?.value?.join(',') }" class="required subscription-keyword-field"/>
</div>

<h2><g:message code="subscription.keywords.header"/></h2>
<div class="info">
	<p><g:message code="subscription.keywords.description"/></p>
</div>
<div class="input">
	<table class="subscription-aliases">
		<tr>
			<td><label for="joinKeywords"><g:message code="subscription.keywords.join"/></label></td>
			<td><g:textField class="aliases validcommas subscription-keyword-field" name="joinKeywords" id="joinKeywords" value="${activityInstanceToEdit?.keywords?.findAll { it.ownerDetail == 'JOIN' }?.value?.join(',') }"/></td>
		</tr>
		<tr>
			<td><label for="leaveKeywords"><g:message code="subscription.keywords.leave"/></label></td>
			<td><g:textField class="aliases validcommas subscription-keyword-field" name="leaveKeywords" id="leaveKeywords" value="${activityInstanceToEdit?.keywords?.findAll { it.ownerDetail == 'LEAVE' }?.value?.join(',') }"/></td>
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
			<g:radio name="defaultAction" value="join" checked="${activityInstanceToEdit?.defaultAction? activityInstanceToEdit?.defaultAction == Subscription.Action.JOIN : true}" class="subscription-default-action"/>
		</li>
		<li>
			<label for="defaultAction"><g:message code="subscription.default.action.leave"/></label>
			<g:radio name="defaultAction" value="leave" checked="${activityInstanceToEdit?.defaultAction? activityInstanceToEdit?.defaultAction == Subscription.Action.LEAVE : false}" class="subscription-default-action"/>
		</li>
		<li>
			<label for="defaultAction"><g:message code="subscription.default.action.toggle"/></label>
			<g:radio name="defaultAction" value="toggle" checked="${activityInstanceToEdit?.defaultAction? activityInstanceToEdit?.defaultAction == Subscription.Action.TOGGLE : false}" class="subscription-default-action"/>
		</li>
	</ul>
</div>
<r:script>
	$(function() {
		$('#disableSorting').live("change", function() {
			if($(this).is(":checked")) {
				$(".subscription-keyword-field,.subscription-default-action").attr("disabled", "disabled");
				$(".subscription-keyword-field,.subscription-default-action").removeClass("required error");
				$(".error").hide();
			} else {
				$(".subscription-keyword-field,.subscription-default-action").attr("disabled", false);
				$(".subscription-keyword-field,.subscription-default-action").addClass("required");
			}
		});
	});
</r:script>

