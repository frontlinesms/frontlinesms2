<div id="tabs-1">
	<h2 class="bold"><g:message code="autoreply.keyword.title"/></h2>
	<g:textField name="keyword" id="keyword" value="${activityInstanceToEdit?.keyword?.value}"/>
	<p><g:message code="autoreply.all.messages"/> <g:checkBox name="blankKeyword" checked="false"/></p>
</div>
<r:script>
	$(document).ready(function(){
	$('#blankKeyword').live("change", function(){
		(this.checked) ? $("#keyword").attr('disabled','disabled') : $("#keyword").attr('disabled',false);
		});
	});
</r:script>
