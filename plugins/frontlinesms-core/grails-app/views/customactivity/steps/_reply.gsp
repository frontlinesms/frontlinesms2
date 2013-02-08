<fsms:step type="reply" stepId="${stepId}">
	<g:textArea id="autoreplyText${stepId}${random}" name="autoreplyText" rows="3" value="${autoreplyText}"/>
	<fsms:magicWand target="autoreplyText${stepId}${random}" controller="autoreply" hidden="true" instance="${activityInstanceToEdit?:null}"/>
</fsms:step>

