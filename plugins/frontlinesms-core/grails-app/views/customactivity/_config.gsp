<div class="info">
	<p><g:message code="customactivity.config.description"/></p>
</div>

<div id="custom-activity-actions-container">
	<label> <h4>Add a step: </h4>
		<select class="dropdown">
			<g:each var="type" in="${['reply', 'join', 'leave', 'webconnectionStep']}">
				<option value="${type}">
					<g:message code="customactivity.step.${type}.add"/>
				</option>
				<fsms:render template="/customactivity/steps/${type}" type="sanchez" id="step-${type}" runtimeVars="random"/>
			</g:each>
		</select>
	</label>
</div>

<g:hiddenField name="jsonToSubmit" />
<ul id="custom-activity-config-container">
	<g:if test="${activityInstanceToEdit}">
		<g:each var="step" in="${activityInstanceToEdit.steps}">
			<fsms:render template="/customactivity/steps/${step.shortName}" model="${step.config}"/>
		</g:each>
	</g:if>
</ul>

