<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${messageInstanceTotal > 0}">
	<table id="messages">
		<thead>
			<tr>
			    <td><g:message code="fmessage.src.label" default="Name"/></td>
			    <td><g:message code="fmessage.text.label" default="Snippet"/></td>
			    <td><g:message code="fmessage.date.label" default="Date"/></td>
			</tr>
		</thead>
		<tbody>
			<g:each in="${messageInstanceList}" status="i" var="m">
			    
				<tr class="${m == messageInstance?'selected':''}">
					<td>${m.src}</td>
					<td>
					  <g:link action="inbox" id="${m.id}">
					    ${m.text}
					  </g:link>
					</td>
					<td><g:formatDate format="dd-MMM-yyyy hh:mm" date="${m.dateCreated}" /></td>
				  
				</tr>
			</g:each>
		</tbody>
	</table> 
</g:if>
<g:else>
	<div id="messages">
		You have no messages saved
	</div>
</g:else>