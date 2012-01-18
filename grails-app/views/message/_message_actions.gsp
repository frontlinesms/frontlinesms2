<script>
	function toggleDropdown() {
		$("#dropdown_options").toggle()
		return false;
	};
</script>
<div id="message-detail-buttons">
	<g:form controller="${params.viewingArchive ? 'archive' : 'message'}" method="POST">
		<g:hiddenField name="messageSection" value="${messageSection}"></g:hiddenField>
		<g:hiddenField name="ownerId" value="${ownerInstance?.id}"></g:hiddenField>
		<g:hiddenField name="messageId" value="${messageInstance?.id}"></g:hiddenField>
		<g:hiddenField name="checkedMessageList" value="${params.checkedMessageList}"></g:hiddenField>
		<g:hiddenField name="viewingArchive" value="${params.viewingArchive}"></g:hiddenField>
		<g:if test="${messageSection == 'result'}">
			<g:hiddenField name="searchId" value="${search.id}"></g:hiddenField>
		</g:if>
		<g:if test="${messageSection == 'pending' && messageInstance.hasFailed}">
			<g:link class="msg-btn btn" elementId="retry" action="retry" params="${[messageId: messageInstance.id]}">Retry</g:link>
		</g:if>
		<g:if test="${messageSection != 'trash'}">
			<g:if test="${messageSection != 'pending'}">
			 	<div id="msg-response-dropdown" class="msg-btn">
					<a id="btn_reply" onclick="messageResponseClick('Reply')">Reply</a>
					<a id='btn_dropdown' href="#" onclick="toggleDropdown();"></a>
				</div>
				<div id="dropdown_options" style='display: none'>
					<a class='dropdown-item' id="btn_forward" onclick="messageResponseClick('Forward')">Forward</a>
				</div>
			</g:if>
			<g:if test="${!messageInstance.messageOwner && !messageInstance.archived}">
				<g:actionSubmit id="archive-msg" class="msg-btn" value="Archive" action="archive"/>
			</g:if>
			<g:elseif test="${!messageInstance.messageOwner && messageInstance.archived}">
				<g:actionSubmit id="unarchive-msg" class="msg-btn" value="Unarchive" action="unarchive"/>
			</g:elseif>
			<g:actionSubmit id="delete-msg" class="msg-btn" value="Delete" action="delete"/>
		</g:if>
		<g:elseif test="${ownerInstance}">
			<g:remoteLink class="msg-btn btn" controller="${(ownerInstance instanceof frontlinesms2.Folder) ? 'folder' : 'poll'}" action="restore" params="[id: ownerInstance?.id]" onSuccess="function() { window.location = location}" >Restore</g:remoteLink>
		</g:elseif>
	</g:form>
</div>
