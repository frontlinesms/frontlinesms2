<%@ page contentType="text/html;charset=UTF-8" %>
<div>
  <h2>Categorize Response</h2>
  <ol id ="poll-actions">
	  <g:each in="${responseList.sort { it.value }}" status="i" var="r">
		  <li>
			  <g:link action="changeResponse" params="[messageSection: 'poll', pollId: pollInstance.id, responseId: r.id]" id ="${messageInstance.id}">${r.value}</g:link>
		  </li>
	  </g:each>
  </ol>
</div>
