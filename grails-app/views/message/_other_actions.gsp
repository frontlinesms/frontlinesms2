<div id='other-actions' class="actions buttons">
	<g:if test="${messageSection == 'poll'}">
		<div id="poll-actions">
			<div class="dropdown">
				<g:hiddenField id="owner-id" name="owner-id" value="${ownerInstance.id}" />
				<g:hiddenField id="response-id" name="response-id" value="${responseInstance.id}" />
				<h2>Categorize Response</h2>
				<select id="categorise_dropdown" >
					<option value="btn-${responseInstance.id}" class="na" onclick="categoriseClickAction(${responseInstance.id});">${responseInstance.value}</option>
					<g:each in="${responseList}" status="i" var="r">
						<g:if test="${r.id != responseInstance.id}">
							<option value="btn-${r.id}" onclick="categoriseClickAction(${r.id});">${r.value}</option>
						</g:if>
					</g:each>
				</select>
			</div>
		</div>
	</g:if>
	<g:if test="${!params['archived']}">
		<div id='move-message' class='dropdown'>
			<select id="move-actions" onchange="moveAction()">
				<option value="na" class="na">Move message to...</option>
				<g:if test="${messageSection != 'inbox'}">
					<option class="inbox" value="inbox">Inbox</option>
				</g:if>
				<g:each in="${pollInstanceList}" status="i" var="p">
					<g:if test="${(messageSection == 'inbox') || (p != ownerInstance)}">
						<option class="poll" value="${p.id}">${p.title}</option>
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
