<div id='move-message'>
	<select class="dropdown extra-msg-btn" name="move-actions" id="move-actions" onchange="moveAction()">
		<option value="na" class="na">Move message to...</option>
		<g:if test="${!(messageSection in ['inbox', 'sent', 'pending'])}">
			<option class="inbox" value="inbox">Inbox</option>
		</g:if>
		<g:each in="${activityInstanceList}" status="i" var="a">
			<g:if test="${a != ownerInstance}">
				<option class="activity" value="${a.id}">${a.name}</option>
			</g:if>
		</g:each>
		<g:each in="${folderInstanceList}" status="i" var="f">
			<g:if test="${f != ownerInstance}">
				<option class="folder" value="${f.id}">${f.name}</option>
			</g:if>
		</g:each>
	</select>
</div>

