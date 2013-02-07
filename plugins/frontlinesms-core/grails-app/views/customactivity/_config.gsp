<div class="info">
	<p><g:message code="customactivity.config.description"/></p>
</div>
<div id="custom-activity-actions-container">
	<a class="btn" id="add-join-action-step">Add sender to group</a>
	<a class="btn" id="add-leave-action-step">Remove sender from group</a>
	<a class="btn" id="add-reply-action-step">Autoreply</a>
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
	#custom-activity-actions-container .btn { margin-left:-5px; }
	#custom-activity-config-container { padding:2px; }
	#custom-activity-config-container .step { padding:5px; margin:3px 0; cursor:move; border-bottom:1px solid #eeeeee; }
	#custom-activity-config-container .step:last-child { border-bottom:none; }
	#custom-activity-config-container .step .remove-command { float:right; padding:5px 0; }
	#custom-activity-config-container .step textarea { display:block; width:98%; }
	#custom-activity-config-container .step-title { width:150px; display:inline-block; padding-left:2px;}
	#custom-activity-config-container .reply-action-step .step-title { width:250px; }
</style>

