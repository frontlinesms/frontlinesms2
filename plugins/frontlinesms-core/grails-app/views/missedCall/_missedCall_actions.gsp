<r:script>
	function toggleDropdown() {
		$("#dropdown_options").toggle()
		return false;
	};
</r:script>

<div id="interaction-detail-buttons">
	<g:if test="${messageSection != 'trash'}">
		<a id="btn_reply" class="msg-btn btn" onclick="mediumPopup.messageResponseClick('Reply')"><g:message code="missedCall.reply" /></a>

		<g:if test="${!interactionInstance.messageOwner?.archived}">
			<g:actionSubmit id="delete-msg" class="msg-btn btn" value="${g.message(code:'fmessage.delete')}" action="delete"/>
		</g:if>
	</g:if>
	<g:elseif test="${ownerInstance}">
		<g:remoteLink class="msg-btn btn" controller="${(ownerInstance instanceof frontlinesms2.Folder) ? 'folder' : 'poll'}" action="restore" params="[id: ownerInstance?.id]" onSuccess="function() { window.location = location}" >
			<g:message code="fmessage.restore" />
		</g:remoteLink>
	</g:elseif>
</div>

