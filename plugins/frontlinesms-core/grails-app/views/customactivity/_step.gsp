<li class="${type} step" index="${stepId}">
	<a class="remove-command">&times;</a>
	<h4><g:message code="customactivity.step.${type}.title"/></h4>
	<g:hiddenField name="stepId" id="stepId-${stepId}-${random}" value="${stepId}" class="customactivity-field"/>
	<g:hiddenField name="stepType" id="stepType-${stepId}-${random}" value="${type}" class="customactivity-field"/>
	${body()}
</li>

