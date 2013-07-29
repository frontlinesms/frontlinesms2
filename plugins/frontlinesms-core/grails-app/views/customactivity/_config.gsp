<div class="info">
	<p><g:message code="customactivity.config.description"/></p>
</div>

<div id="custom-activity-actions-container">
	<label>
		<h4>Add a step:</h4>
		<fsms:select class="dropdown" name="custom_activity_select" from="${['reply', 'join', 'leave', 'webconnectionStep', 'forward']}" noSelection="${['':'Select an action']}" onchange="selectmenuTools.snapback(this);" optionValue="${{g.message(code:'customactivity.step.' + it + '.add')}}">
			<g:each var="type" in="${['join', 'leave', 'reply', 'forward', 'webconnectionStep']}">
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

