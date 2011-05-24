<%@ page import="frontlinesms2.Contact" %>
<div>
  <h2>Move messages to</h2>
  <ol id ="message-actions">
	  <g:each in="${pollInstanceList}" status="i" var="p">
		  <li>
			  <g:link action="move" id="${p.id}" params="[]">${p.title}</g:link>
		  </li>
	  </g:each>
  </ol>
</div>