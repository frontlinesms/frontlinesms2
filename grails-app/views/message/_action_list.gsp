<div>
	<select id="message-actions">
		<option value="na" class="na">Move message to...</option>
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