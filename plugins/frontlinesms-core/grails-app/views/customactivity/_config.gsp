<div class="info">
	<p><g:message code="customactivity.config.description"/></p>
</div>
<div id="custom-activity-actions-container">
	<div class="custom-activity-action-step btn" id="add-join-action-step">Join</div>
	<div class="custom-activity-action-step btn" id="add-leave-action-step">Leave</div>
	<div class="custom-activity-action-step btn" id="add-reply-action-step">Reply</div>
</div>
<div id="custom-activity-config-container">
	<g:hiddenField name="jsonToSubmit" />
	<fsms:savedActionSteps activityId="${activityInstanceToEdit?.id}"/>
</div>

<style type="text/css">
	#custom-activity-config-container {
		width: 100%;
		padding:2px;
	}

	#custom-activity-actions-container {
		width: 100%;
		background-color: #eeddff;
		margin-top: 3px;
		padding:2px;
	}

	.custom-activity-action-step {
		background-color: #ddaaff;
		padding: 5px;
		display: inline-block;
	}
	.step {
		padding:5px;
		background-color: #eeeeee;
		margin-bottom: 2px;
	}
</style>