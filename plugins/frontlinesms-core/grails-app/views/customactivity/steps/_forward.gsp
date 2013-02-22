<fsms:step type="forward" stepId="${stepId}">
	<div class='input'>
		<fsms:messageComposer name="sentMessageText" rows="3" textAreaId="sentMessageText${stepId}${random}" target="sentMessageText${stepId}${random}" controller="autoforward" value="${sentMessageText}"/>
		<fsms:recipientSelector class="customactivity-field" groups="${groups}" smartGroups="${smartGroups}" contacts="${contacts}" addresses="${addresses}" />
	</div>
</fsms:step>
