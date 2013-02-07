<li class="${type} step" index="${stepId}">
	<a class="remove-command"></a>
	<h4><g:message code="customactivity.step.${type}.title"/></h4>
	<g:hiddenField name="stepId" value="${stepId}"/>
	<g:hiddenField name="stepType" value="${type}"/>
	${body()}
</li>

