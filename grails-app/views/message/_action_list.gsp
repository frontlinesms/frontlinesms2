<%@ page import="frontlinesms2.Contact" %>
<div>
  <h2>Move messages to</h2>
  <ol id ="message-actions">
	  <g:each in="${pollInstanceList}" status="i" var="p">
		  <li>
			  <g:if test="${p != pollInstance}">
				  <g:link action="move" params="[pollId: p.id, oldPollId: pollInstance.id]" id ="${messageInstance.id}">${p.title}</g:link>
			  </g:if>
		  </li>
	  </g:each>
  </ol>
</div>