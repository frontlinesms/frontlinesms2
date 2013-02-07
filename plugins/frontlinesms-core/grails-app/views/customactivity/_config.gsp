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
