<%@ page import="frontlinesms2.Group" %>
<li class='leave-action-step step' index='${stepId}'>
	<div><a class='remove-command remove-step'></a></div>
	<span class='step-title'>Remove sender from group</span>
	<g:hiddenField name='stepId' value="${stepId}"/>
	<g:hiddenField name='stepType' value='leave'/>
	<g:select name='group' id="" noSelection="${['null':'Select One...']}" from="${Group.getAll()}" value="${groupId}" optionKey="id" optionValue="name" class="notnull"/>
</li>
