<div id='other-actions' class="actions buttons">
	<g:if test="${messageSection == 'poll'}">
		<div id="poll-actions">
			<div>
				<g:hiddenField name="owner-id" value="${ownerInstance.id}" />
				<g:hiddenField name="responseId" value="${responseInstance?.id}" />
				<select class="dropdown extra-msg-btn" name="categorise_dropdown" id="categorise_dropdown" onchange="categorizeClickAction()">
					<option value="na" class="na">Categorize response</option>
					<option value="btn-${responseInstance?.id}" class="na">${responseInstance?.value}</option>
					<g:each in="${responseList}" status="i" var="r">
						<g:if test="${r.id != responseInstance?.id}">
							<option value="btn-${r.id}" >${r.value}</option>
						</g:if>
					</g:each>
				</select>
			</div>
		</div>
	</g:if>
	<g:if test="${messageSection != 'pending'}">
		<div id='move-message'>
			<select class="dropdown extra-msg-btn" name="move-actions" id="move-actions" onchange="moveAction()">
				<option value="na" class="na">Move message to...</option>
				<g:if test="${messageSection != 'inbox'}">
					<option class="inbox" value="inbox">Inbox</option>
				</g:if>
				<g:each in="${pollInstanceList}" status="i" var="p">
					<g:if test="${(messageSection == 'inbox') || (p != ownerInstance)}">
						<option class="poll" value="${p.id}">${p.title}</option>
					</g:if>
				</g:each>
				<g:each in="${announcementInstanceList}" status="i" var="a">
					<g:if test="${(messageSection == 'inbox') || (a != ownerInstance)}">
						<option class="announcement" value="${a.id}">${a.name}</option>
					</g:if>
				</g:each>
				<g:each in="${folderInstanceList}" status="i" var="f">
					<g:if test="${(messageSection == 'inbox') || (f != ownerInstance)}">
						<option class="folder" value="${f.id}">${f.name}</option>
					</g:if>
				</g:each>
			</select>
		</div>
	</g:if>
</div>
