<%@ page import="frontlinesms2.Contact" %>
<div>
  <h2>Move message to</h2>
  <ol id ="message-actions">
	  <g:each in="${pollInstanceList}" status="i" var="p">
		  <li>
			  <g:if test="${messageSection == 'inbox'}">
				  <g:link action="move" params="[messageSection: 'poll', messageId: messageInstance.id, ownerId: p.id]">${p.title}</g:link>
			  </g:if>
			  <g:elseif test="${p != ownerInstance}">
				  <g:link action="move" params="[messageSection: 'poll', messageId: messageInstance.id, ownerId: p.id]">${p.title}</g:link>
			  </g:elseif>
		  </li>
	  </g:each>
	  <g:each in="${folderInstanceList}" status="i" var="f">
		  <li>
			  <g:if test="${messageSection == 'inbox'}">
				  <g:link action="move" params="[messageSection: 'folder', messageId: messageInstance.id, ownerId: f.id]">${f.value}</g:link>
			  </g:if>
			  <g:elseif test="${f != ownerInstance}">
				  <g:link action="move" params="[messageSection: 'folder', messageId: messageInstance.id, ownerId: f.id]">${f.value}</g:link>
			  </g:elseif>
		  </li>
	  </g:each>
  </ol>
</div>