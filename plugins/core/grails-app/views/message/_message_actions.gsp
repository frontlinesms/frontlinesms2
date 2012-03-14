<script>
	function toggleDropdown() {
		$("#dropdown_options").toggle()
		return false;
	};
</script>
<div id="message-detail-buttons">
	<g:form controller="${params.controller}" params="[messageSection: messageSection, ownerId: ownerInstance?.id, messageId: messageInstance?.id, checkedMessageList: checkedMessageList, searchId: search?.id]">
		<g:if test="${messageSection != 'trash'}">
	 		<g:if test="${messageSection == 'sent'}">
				<g:link class="msg-btn btn" elementId="btn_reply" action="retry" params="${[messageId: messageInstance.id]}">Resend</g:link>
			</g:if>
			<g:elseif test="${messageSection == 'pending' && messageInstance.hasFailed}">
				<g:link class="msg-btn btn" elementId="btn_reply" action="retry" params="${[messageId: messageInstance.id]}">Retry</g:link>
			</g:elseif>
			<g:elseif test="${messageSection == 'pending'}">
				<g:link class="msg-btn btn" elementId="btn_reply" action="retry" params="${[messageId: messageInstance.id]}">Resend</g:link>
			</g:elseif>
			<g:else>
				<a id="btn_reply" class="msg-btn btn" onclick="messageResponseClick('Reply')">Reply</a>
			</g:else>
			<a id="btn_forward"class="msg-btn btn" onclick="messageResponseClick('Forward')">Forward</a>
			<g:if test="${!messageInstance.messageOwner && !messageInstance.archived}">
				<g:actionSubmit id="archive-msg" class="msg-btn" value="Archive" action="archive"/>
			</g:if>
			<g:elseif test="${!messageInstance.messageOwner && messageInstance.archived}">
				<g:actionSubmit id="unarchive-msg" class="msg-btn" value="Unarchive" action="unarchive"/>
			</g:elseif>
			<g:if test="${messageSection != 'pending'}">
				<g:actionSubmit id="delete-msg" class="msg-btn" value="Delete" action="delete"/>
			</g:if>
		</g:if>
		<g:elseif test="${ownerInstance}">
			<g:remoteLink class="msg-btn btn" controller="${(ownerInstance instanceof frontlinesms2.Folder) ? 'folder' : 'poll'}" action="restore" params="[id: ownerInstance?.id]" onSuccess="function() { window.location = location}" >Restore</g:remoteLink>
		</g:elseif>
	</g:form>
</div>
