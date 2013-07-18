<div class="info">
	<p><g:message code="customactivity.config.description"/></p>
</div>

<div id="custom-activity-actions-container" class="button-bar">
	<label> <h4>Add a step: </h4>
		<fsms:select class="dropdown" name="custom_activity_select" from="${['reply', 'join', 'leave', 'webconnectionStep']}" noSelection="${['':'Select an action']}" onchange="selectmenuTools.snapback(this);" optionValue="${{g.message(code:'customactivity.step.' + it + '.add')}}">
			<a class="btn" onclick="custom_activity.addStep('${type}')"><g:message code="customactivity.step.${type}.add"/></a>
			<g:each var="type" in="${['reply', 'join', 'leave', 'webconnectionStep', forward']}">
				<fsms:render template="/customactivity/steps/${type}" type="sanchez" id="step-${type}" runtimeVars="random"/>
			</g:each>
		</fsms:select>
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

