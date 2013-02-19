<div class="info">
	<p><g:message code="customactivity.config.description"/></p>
</div>

<div id="custom-activity-actions-container" class="button-bar">
	<g:each var="type" in="${['join', 'leave', 'reply', 'forward']}">
		<a class="btn" onclick="custom_activity.addStep('${type}')"><g:message code="customactivity.step.${type}.add"/></a>
		<fsms:render template="/customactivity/steps/${type}" type="sanchez" id="step-${type}" runtimeVars="random"/>
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

