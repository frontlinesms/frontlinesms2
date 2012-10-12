<div class="generic_sorting_tab">
	<h2><g:message code="activity.generic.sort.header"/></h2>
	<div class="info">
		<p><g:message code="activity.generic.sort.description"/></p>
	</div>
	<div class="input">
		<label for="keywords"><g:message code="activity.generic.keywords.title"/></label>
		<g:textField name="keywords" value="${activityInstanceToEdit?.keywords? activityInstanceToEdit?.keywords*.value.join(',') : ''}" disabled="${activityInstanceToEdit && !activityInstanceToEdit?.keywords}"/>
	</div>
	<div class="input optional">
		<label for="blankKeyword"><g:message code="activity.generic.no.keywords"/></label>
		<g:checkBox name="blankKeyword" checked="${activityInstanceToEdit && !activityInstanceToEdit?.keywords}"/>
	</div>
	<r:script>
	$(function() {
		$('#blankKeyword').live("change", function() {
			if($(this).is(":checked")) {
				$("#keywords").attr("disabled", "disabled");
				$("#keywords").removeClass("required error");
				$(".error").hide();
			} else {
				$("#keywords").attr("disabled", false);
				$("#keywords").addClass("required");
			}
		});
	});
	</r:script>
</div>
