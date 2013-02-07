<div class="info">
	<p><g:message code="customactivity.config.description"/></p>
</div>
<div id="custom-activity-actions-container">
	<div class="custom-activity-step btn" id="add-join-action-step">Add sender to group</div>
	<div class="custom-activity-step btn" id="add-leave-action-step">Remove sender from group</div>
	<div class="custom-activity-step btn" id="add-reply-action-step">Autoreply</div>
</div>

<fsms:render template="/customactivity/steps/join" type="sanchez" id="step-join" runtimeVars="stepId"/>
<fsms:render template="/customactivity/steps/leave" type="sanchez" id="step-leave"/>
<fsms:render template="/customactivity/steps/reply" type="sanchez" id="step-reply"/>

<g:hiddenField name="jsonToSubmit" />
<ul id="custom-activity-config-container">
	<g:if test="${activityInstanceToEdit}">
		<g:each var="step" in="${activityInstanceToEdit.steps}">
			<fsms:render template="/customactivity/steps/${step.shortName}" model="${step.config}"/>
		</g:each>
	</g:if>
</ul>

<style type="text/css">
	#custom-activity-config-container {
		padding:2px;
		clear: left;
	}
	#custom-activity-config-container > :last-child { border-bottom:none; }

	#custom-activity-actions-container {
		background-image: url("/frontlinesms-core/static/images/button/standard.png");
		background-repeat: repeat-x;
		border: none;
		border: solid #6b6b6b 1px;
		border-top: none;
		width: 100%;
		height: 23px;
	}

	.custom-activity-step {
		float: left;
		border:none;
		border-right: 1px solid #bbb; 
	}
	.step {
		padding:5px;
		margin: 3px 0;
		cursor:move;
		border-bottom: 1px solid #eeeeee;
	}
	.step .remove-command { float: right; padding: 5px 0; }
	.step textarea { display: block; width: 98%; }
	.step-title { width: 150px; display: inline-block; padding-left: 2px;}
	.reply-action-step .step-title { width: 250px; }
</style>
