<select class="dropdown extra-msg-btn" name="move-actions" id="move-actions" onchange="moveAction(); selectmenuTools.snapback(this)">
	<option value="na" class="na"><g:message code="fmessage.move.to.header"/></option>
	<g:if test="${!(messageSection in ['inbox', 'sent', 'pending'])}">
		<option class="inbox" value="inbox"><g:message code="fmessage.move.to.inbox"/></option>
	</g:if>
	<g:each in="${activityInstanceList}" var="a">
		<g:if test="${a != ownerInstance}">
			<option class="activity ${a.shortName}" value="${a.id}">${a.name}</option>
		</g:if>
	</g:each>
	<g:each in="${folderInstanceList}" var="f">
		<g:if test="${f != ownerInstance}">
			<option class="folder" value="${f.id}">${f.name}</option>
		</g:if>
	</g:each>
</select>