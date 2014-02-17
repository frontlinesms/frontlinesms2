<div class="other-actions" class="actions buttons">
	<g:if test="${messageSection == 'activity' && ownerInstance}">
		<fsms:render template="/activity/${ownerInstance.shortName}/message_actions"/>
	</g:if>
	<g:if test="${(ownerInstance && !(ownerInstance?.archived)) || !(messageInstance?.archived)}">
		<fsms:render template="/interaction/move_message"/>
	</g:if>
</div>
