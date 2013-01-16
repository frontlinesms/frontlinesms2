<div id="custom-activity-config-container">
	<g:hiddenField name="jsonToSubmit" />
	<fsms:savedActionSteps activityId="${activityInstanceToEdit?.id}"/>
</div>
<div id="custom-activity-actions-container">
	<div class="custom-activity-action-step" id="add-join-action-step">Join</div>
	<div class="custom-activity-action-step" id="add-leave-action-step">Leave</div>
	<div class="custom-activity-action-step" id="add-reply-action-step">Reply</div>
</div>

<style type="text/css">
	#custom-activity-config-container {
		height:330px;
		width: 100%;
		background-color: #eeddff
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
		background-color: lightgray;
		margin-bottom: 2px;
	}
</style>