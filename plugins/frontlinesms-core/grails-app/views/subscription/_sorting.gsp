<%@ page import="frontlinesms2.Subscription" %>
<h2><g:message code="subscription.sorting"/></h2>
<div class="info">
	<p><g:message code="activity.generic.sort.description"/></p>
</div>
<div class="input">
	<ul class="select">
		<li>
			<g:checkBox name="disableSorting" checked="${activityInstanceToEdit ? !activityInstanceToEdit?.keywords : false}"/>
			<label for="leaveKeywords"><g:message code="subscription.sorting.disable"/></label>
		</li>
	</ul>
</div>
<h2><g:message code="subscription.top.keyword.header"/></h2>
<div class="info">
	<p><g:message code="subscription.top.keyword.description"/></p>
</div>

<div class="input">
	<g:textField name="topLevelKeywords" value="${activityInstanceToEdit?.keywords?.findAll { it.isTopLevel && !it.ownerDetail}?.value?.join(',') }" class="validcommas sorting-generic-unique sorting-generic-no-spaces subscription-keyword-field" disabled="${activityInstanceToEdit ? !activityInstanceToEdit?.keywords : false}"
	 onkeyup="updateAliasTips()"/>
</div>

<h2><g:message code="subscription.keywords.header"/></h2>
<div class="info">
	<p><g:message code="subscription.keywords.description"/></p>
</div>
<div class="input">
	<table class="subscription-aliases">
		<tr>
			<td><label for="joinKeywords"><g:message code="subscription.keywords.join"/></label></td>
			<td><g:textField class="keywords validcommas sorting-generic-unique sorting-generic-no-spaces subscription-keyword-field" name="joinKeywords" id="joinKeywords" value="${activityInstanceToEdit?.keywords?.findAll { it.ownerDetail == 'JOIN' }?.value?.join(',') }" disabled="${activityInstanceToEdit ? !activityInstanceToEdit?.keywords : false}" onkeyup="updateAliasTips()"/></td>
		</tr>
		<tr>
			<td> <p class="info"> <g:message code="subscription.sorting.tip.label"/> </p> </td>
			<td><label id="joinHelperMessage"><g:message code="subscription.sorting.tip.prompt"/></label></td>
		</tr>
		<tr>
			<td><label for="leaveKeywords"><g:message code="subscription.keywords.leave"/></label></td>
			<td><g:textField class="keywords validcommas sorting-generic-unique sorting-generic-no-spaces subscription-keyword-field" name="leaveKeywords" id="leaveKeywords" value="${activityInstanceToEdit?.keywords?.findAll { it.ownerDetail == 'LEAVE' }?.value?.join(',') }" disabled="${activityInstanceToEdit ? !activityInstanceToEdit?.keywords : false}" onkeyup="updateAliasTips()"/></td>
		</tr>
		<tr>
			<td> <p class="info"> <g:message code="subscription.sorting.tip.label"/> </p> </td>
			<td><label id="leaveHelperMessage"><g:message code="subscription.sorting.tip.prompt" /></label></td>
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

	function generateKeywordTips(keyword, alias){
		var aliases = alias.split(",");
		var keywords = keyword.split(',');
		var tipArray = [];
		var n = 0;
		$(aliases).each(function(i, v) {
			$(keywords).each(function(index, key){
				tipArray[n] = key + " " + v;
				n++;
			});
		});
		var tip =  tipArray.splice(0,3).join(', ').trim();
		return (tip.length > 0 ? tip : "${g.message(code: 'subscription.sorting.tip.prompt')}")
	}

	function updateJoinTip(topKeyword){
		var joinAlias = $('input#joinKeywords').val();
		var tip = generateKeywordTips(topKeyword,joinAlias);
		$('#joinHelperMessage').html(tip);
	}

	function updateLeaveTip(topKeyword){
		var leaveAlias = $('input#leaveKeywords').val();
		var tip = generateKeywordTips(topKeyword,leaveAlias);
		$('#leaveHelperMessage').html(tip);
	}

	function updateAliasTips(){
		var topKeyword = $('input#topLevelKeywords').val();
		toggleDefaultAction(topKeyword);
		updateJoinTip(topKeyword);
		updateLeaveTip(topKeyword);
	}

	function toggleDefaultAction(topKeyword){
		if(topKeyword.trim().length > 0){
			$('input#defaultAction').attr('disabled',false);
		}else{
			$('input#defaultAction').attr('disabled',true);
		}
	}
</r:script>

