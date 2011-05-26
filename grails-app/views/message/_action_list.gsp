<%@ page import="frontlinesms2.Contact" %>
<div>
  <h2>Move message to</h2>
  <ol id ="message-actions">
	  <g:each in="${pollInstanceList}" status="i" var="p">
		  <li>
			  <g:if test="${messageSection == 'inbox'}">
				  <g:link action="move" params="[pollId: p.id]" id ="${messageInstance.id}">${p.title}</g:link>
			  </g:if>
			  <g:elseif test="${p != pollInstance}">
				  <g:link action="move" params="[pollId: p.id]" id ="${messageInstance.id}">${p.title}</g:link>
			  </g:elseif>
		  </li>
	  </g:each>
  </ol>
</div>