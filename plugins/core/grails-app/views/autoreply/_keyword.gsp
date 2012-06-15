<div class="input">
	<label for="keyword"><g:message code="autoreply.keyword.title"/></label>
	<g:textField name="keyword" id="keyword" value="${activityInstanceToEdit?.keyword?.value}"/>
</div>
<div class="input optional">
	<label for="blankKeyword"><g:message code="autoreply.all.messages"/>
	<g:checkBox name="blankKeyword"/>
</div>
<r:script>
$(function() {
	$('#blankKeyword').live("change", function() {
		$("#keyword").attr("disabled", this.checked? "disabled" : false);
	});
});
</r:script>
