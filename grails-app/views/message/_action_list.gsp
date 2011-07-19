<div>
  <h2>Move message to</h2>
  <ol id ="message-actions">
	  <g:each in="${pollInstanceList}" status="i" var="p">
		  <li>
			  <g:if test="${messageSection=='inbox' || p!=ownerInstance}">
				  <g:remoteLink controller="message" action="move" onSuccess="reload()" params="[messageSection: 'poll', messageId: messageInstance.id, ownerId: p.id]">${p.title}</g:remoteLink>
			  </g:if>
		  </li>
	  </g:each>
	  <g:each in="${folderInstanceList}" status="i" var="f">
		  <li>
			  <g:if test="${messageSection=='inbox' || f!=ownerInstance}">
				  <g:remoteLink controller="message" action="move" onSuccess="reload()" params="[messageSection: 'folder', messageId: messageInstance.id, ownerId: f.id]">${f.name}</g:remoteLink>
			  </g:if>
		  </li>
	  </g:each>
  </ol>
</div>

<script>
	function reload() {
		location.reload();
	}
</script>