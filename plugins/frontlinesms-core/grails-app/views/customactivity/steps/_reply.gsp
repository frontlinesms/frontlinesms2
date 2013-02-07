<li class='reply-action-step step' index='${stepId}'>
	<div><a class='remove-command remove-step'></a></div>
	<span class='step-title'>Enter message to autoreply to sender</span>
	<g:hiddenField name='stepId' value='${stepId}'/>
	<g:hiddenField name='stepType' value='reply'/>
	<g:textArea name='autoreplyText' rows='3' value='${autoreplyText}'/>
</li>

