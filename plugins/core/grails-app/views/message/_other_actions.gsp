<%@ page import="frontlinesms2.*" %>
<div class="other-actions" class="actions buttons">
	<g:if test="${messageSection == 'activity' && ownerInstance}">
		<fsms:render template="/activity/${ownerInstance.shortName}/message_actions"/>
	</g:if>
	<fsms:render template="/message/move_message"/>
</div>
