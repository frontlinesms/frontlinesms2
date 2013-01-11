<%@ page import="frontlinesms2.Subscription" %>
<h2><g:message code="subscription.sorting.header"/></h2>
<div class="input">
	<ul class="select">
		<li>
			<label for="sorting"><g:message code="poll.autosort.description"/></label>
			<g:radio name="sorting" id="yesAutosort" value="true" checked="${activityInstanceToEdit?.keywords? activityInstanceToEdit.keywords as boolean: ''}"/>
		</li>
		<li>
			<label for="sorting"><g:message code="poll.autosort.no.description"/></label>
			<g:radio name="sorting" id="noAutosort" value="false" checked="${activityInstanceToEdit?.keywords? '': true}"/>
		</li>
	</ul>
</div>

<h2><g:message code="subscription.keyword.header"/></h2>
<div class="info">
	<p><g:message code="subscription.top.keyword.description"/></p>
	<p><g:message code="subscription.top.keyword.more.description"/></p>
</div>

<div>
	<label for="topLevelKeywords"><g:message code="poll.sort.toplevel.keyword.label"/></label>
	<g:textField placeholder="${g.message(code:'subscription.sorting.example.toplevel')}" name="topLevelKeywords" value="${activityInstanceToEdit?.keywords?.findAll { it.isTopLevel && !it.ownerDetail}?.value?.join(',') }" class="validcommas sorting-generic-unique sorting-generic-no-spaces subscription-keyword-field" disabled="${activityInstanceToEdit ? !activityInstanceToEdit?.keywords : false}"/>
</div>

<div class="info">
	<g:message code="subscription.keywords.header"/>
	<p><g:message code="subscription.keywords.description"/></p>
</div>
<div class="input">
	<table class="subscription-aliases">
		<tr>
			<td><label for="joinKeywords"><g:message code="subscription.keywords.join"/></label></td>
			<td><g:textField placeholder="${g.message(code:'subscription.sorting.example.join')}" class="keywords validcommas sorting-generic-unique sorting-generic-no-spaces subscription-keyword-field" name="joinKeywords" id="joinKeywords" value="${activityInstanceToEdit?.keywords?.findAll { it.ownerDetail == 'JOIN' }?.value?.join(',') }" disabled="${activityInstanceToEdit ? !activityInstanceToEdit?.keywords : false}"/></td>
		</tr>
		<tr>
			<td><label for="leaveKeywords"><g:message code="subscription.keywords.leave"/></label></td>
			<td><g:textField placeholder="${g.message(code:'subscription.sorting.example.leave')}" class="keywords validcommas sorting-generic-unique sorting-generic-no-spaces subscription-keyword-field" name="leaveKeywords" id="leaveKeywords" value="${activityInstanceToEdit?.keywords?.findAll { it.ownerDetail == 'LEAVE' }?.value?.join(',') }" disabled="${activityInstanceToEdit ? !activityInstanceToEdit?.keywords : false}"/></td>
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
			<g:radio name="defaultAction" value="join" checked="${activityInstanceToEdit?.defaultAction? activityInstanceToEdit?.defaultAction == Subscription.Action.JOIN : true}" class="subscription-default-action" disabled="${activityInstanceToEdit ? !activityInstanceToEdit?.keywords : false}"/>
		</li>
		<li>
			<label for="defaultAction"><g:message code="subscription.default.action.leave"/></label>
			<g:radio name="defaultAction" value="leave" checked="${activityInstanceToEdit?.defaultAction? activityInstanceToEdit?.defaultAction == Subscription.Action.LEAVE : false}" class="subscription-default-action" disabled="${activityInstanceToEdit ? !activityInstanceToEdit?.keywords : false}"/>
		</li>
		<li>
			<label for="defaultAction"><g:message code="subscription.default.action.toggle"/></label>
			<g:radio name="defaultAction" value="toggle" checked="${activityInstanceToEdit?.defaultAction? activityInstanceToEdit?.defaultAction == Subscription.Action.TOGGLE : false}" class="subscription-default-action" disabled="${activityInstanceToEdit ? !activityInstanceToEdit?.keywords : false}"/>
		</li>
	</ul>
</div>
<r:script>
	$(function() {
		$('input[name=sorting]').live("change", function() {
			if($(this).attr("value") == "false") {
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

