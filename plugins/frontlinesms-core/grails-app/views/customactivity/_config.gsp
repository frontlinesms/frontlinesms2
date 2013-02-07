<div class="info">
	<p><g:message code="customactivity.config.description"/></p>
</div>

<div id="custom-activity-actions-container">
	<g:each var="type" in="${['join', 'leave', 'reply']}">
		<fsms:render template="/customactivity/steps/${type}" type="sanchez" id="step-${type}" runtimeVars="stepId"/>
		<a class="btn" onclick="custom_activity.addStep('${type}')"><g:message code="customactivity.step.${type}.add"/></a>
	</g:each>
</div>

<g:hiddenField name="jsonToSubmit" />
<ul id="custom-activity-config-container">
	<g:if test="${activityInstanceToEdit}">
		<g:each var="step" in="${activityInstanceToEdit.steps}">
			<fsms:render template="/customactivity/steps/${step.shortName}" model="${step.config}"/>
		</g:each>
	</g:if>
</ul>

<style type="text/css">
	#custom-activity-actions-container .btn { margin-left:-5px; }
	#custom-activity-config-container { padding:2px; }
	#custom-activity-config-container .step { padding:5px; margin:3px 0; cursor:move; border-bottom:1px solid #eeeeee; }
	#custom-activity-config-container .step:last-child { border-bottom:none; }
	#custom-activity-config-container .step .remove-command { float:right; padding:5px 0; }
	#custom-activity-config-container .step textarea { display:block; width:98%; }
	#custom-activity-config-container .step-title { width:150px; display:inline-block; padding-left:2px;}
	#custom-activity-config-container .reply-action-step .step-title { width:250px; }
</style>

