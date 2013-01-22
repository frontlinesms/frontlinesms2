<%@ page import="frontlinesms2.Group" %>
<script id="step-leave" type="text/x-sanchez-template">
	<li class='leave-action-step step' index='{{stepId}}'>
		<div><a class='remove-command remove-step'></a></div>
		<span>Leave Group</span>
		<g:hiddenField name='stepId' value="{{stepId}}"/>
		<g:hiddenField name='stepType' value='join'/>
		<g:select name='group' id="" noSelection="${['null':'Select One...']}" from="${Group.getAll()}" value="{{groupId}}" optionKey="id" optionValue="name" class="notnull"/>
	</li>
</script>