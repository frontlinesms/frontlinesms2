<div class="info">
	<p><g:message code="customactivity.config.description"/></p>
</div>
<div id="custom-activity-actions-container">
	<div class="custom-activity-step btn" id="add-join-action-step">Join</div>
	<div class="custom-activity-step btn" id="add-leave-action-step">Leave</div>
	<div class="custom-activity-step btn" id="add-reply-action-step">Reply</div>
	<div class="custom-activity-step btn" id="add-webconnection-action-step">Reply</div>
</div>

<fsms:render template="/customactivity/steps/join" type="sanchez" id="step-join" runtimeVars="stepId"/>
<fsms:render template="/customactivity/steps/leave" type="sanchez" id="step-leave"/>
<fsms:render template="/customactivity/steps/reply" type="sanchez" id="step-reply"/>
<fsms:render template="/customactivity/steps/webconnectionStep" type="sanchez" id="step-webconnectionStep"/>

<g:hiddenField name="jsonToSubmit" />
<ul id="custom-activity-config-container">
	<g:if test="${activityInstanceToEdit}">
		<g:each var="step" in="${activityInstanceToEdit.steps}">
			<fsms:render template="steps/${step.shortName}" model="${step.config}"/>
		</g:each>
	</g:if>
</ul>

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

	.custom-activity-step {
		background-color: #ddaaff;
		padding: 5px;
		display: inline-block;
	}
	.step {
		padding:5px;
		background-color: #eeeeee;
		margin-bottom: 2px;
		cursor:move;
	}
</style>
