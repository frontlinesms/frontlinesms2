<fsms:step type="reply" stepId="${stepId}">
	<div class='input'>
		<fsms:messageComposer name="autoreplyText" rows="3" textAreaId="autoreplyText${stepId}${random}" target="autoreplyText${stepId}${random}" controller="autoreply"/>
	</div>
</fsms:step>
