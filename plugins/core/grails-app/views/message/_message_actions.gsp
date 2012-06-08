<r:script>
	function toggleDropdown() {
		$("#dropdown_options").toggle()
		return false;
	};
</r:script>

<div id="message-detail-buttons">
	<g:if test="${messageSection != 'trash'}">
		<g:if test="${messageSection == 'pending' && messageInstance.hasFailed}">
			<g:link class="msg-btn btn" elementId="btn_reply" action="retry" params="${[messageId: messageInstance.id]}"><g:message code="fmessage.retry"/></g:link>
		</g:if>
		<g:elseif test="${messageSection == 'pending'}">
			<g:link class="msg-btn btn" elementId="btn_reply" action="retry" params="${[messageId: messageInstance.id]}"><g:message code="fmessage.resend" /></g:link>
		</g:elseif>
		<g:else>
			<a id="btn_reply" class="msg-btn btn" onclick="messageResponseClick('Reply')"><g:message code="fmessage.reply" /></a>
		</g:else>

		<a id="btn_forward" class="msg-btn btn" onclick="messageResponseClick('Forward')"><g:message code="fmessage.forward" /></a>

		<g:if test="${!messageInstance.messageOwner}">
			<g:if test="${!messageInstance.archived}">
				<g:actionSubmit id="archive-msg" class="msg-btn btn" value="${g.message(code:'fmessage.archive')}" action="archive"/>
			</g:if>
			<g:else>
				<g:actionSubmit id="unarchive-msg" class="msg-btn btn" value="${g.message(code:'fmessage.unarchive')}" action="unarchive"/>
			</g:else>
		</g:if>

		<g:if test="${messageSection != 'pending'}">
			<g:actionSubmit id="delete-msg" class="msg-btn btn" value="${g.message(code:'fmessage.delete')}" action="delete"/>
		</g:if>
	</g:if>
	<g:elseif test="${ownerInstance}">
		<g:remoteLink class="msg-btn btn" controller="${(ownerInstance instanceof frontlinesms2.Folder) ? 'folder' : 'poll'}" action="restore" params="[id: ownerInstance?.id]" onSuccess="function() { window.location = location}" >
			<g:message code="fmessage.restore" />
		</g:remoteLink>
	</g:elseif>
</div>

