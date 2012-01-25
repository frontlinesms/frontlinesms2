<div id='other-actions' class="actions buttons">
	<g:if test="${messageSection == 'activity' && ownerInstance.type == 'poll'}">
		<div id="poll-actions">
			<div>
				<g:hiddenField name="owner-id" value="${ownerInstance.id}" />
				<g:hiddenField name="responseId" value="${responseInstance?.id}" />
				<select class="dropdown extra-msg-btn" name="categorise_dropdown" id="categorise_dropdown" onchange="categorizeClickAction()">
					<option value="na" class="na">Categorize response</option>
					<g:each in="${ownerInstance.responses}" status="i" var="r">
						<option value="btn-${r.id}" >${r.value}</option>
					</g:each>
				</select>
			</div>
		</div>
	</g:if>
	<div id='move-message'>
		<select class="dropdown extra-msg-btn" name="move-actions" id="move-actions" onchange="moveAction()">
			<option value="na" class="na">Move message to...</option>
			<g:if test="${messageSection != 'inbox'}">
				<option class="inbox" value="inbox">Inbox</option>
			</g:if>
			<g:each in="${activityInstanceList}" status="i" var="a">
				<g:if test="${(messageSection == 'inbox') || (a != ownerInstance)}">
					<option class="activity" value="${a.id}">${a.name}</option>
				</g:if>
			</g:each>
			<g:each in="${folderInstanceList}" status="i" var="f">
				<g:if test="${(messageSection == 'inbox') || (f != ownerInstance)}">
					<option class="folder" value="${f.id}">${f.name}</option>
				</g:if>
			</g:each>
		</select>
	</div>
</div>
