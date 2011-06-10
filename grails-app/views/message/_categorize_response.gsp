<%@ page contentType="text/html;charset=UTF-8" %>
<div  id ="poll-actions">
  <h2>Categorize Response</h2>
  <ol>
	  <g:each in="${responseList}" status="i" var="r">
		  <li>
			  <g:link action="changeResponse" params="[messageSection: 'poll', ownerId: ownerInstance.id, responseId: r.id]" id ="${messageInstance.id}">${r.value}</g:link>
		  </li>
	  </g:each>
  </ol>
</div>
