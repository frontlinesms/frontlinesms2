  <%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${messageInstanceTotal > 0}">
	<table id="messages">
		<thead>
			<tr>
			    <td><g:message code="fmessage.src.label" default="From"/></td>
			    <td><g:message code="fmessage.text.label" default="Message"/></td>
			    <td><g:message code="fmessage.date.label" default="Date"/></td>
			</tr>
		</thead>
		<tbody>
			<g:each in="${messageInstanceList.sort { it.dateCreated } }" status="i" var="m">
				<tr>
					<td>
							  ${m.displaySrc}
					</td>
					<td>
							  ${m.displayText}
					</td>
					<td>
							<g:formatDate format="dd-MMM-yyyy hh:mm" date="${m.dateCreated}" />
					</td>
				</tr>
			</g:each>
		</tbody>
	</table> 
</g:if>
<g:else>
	<div id="messages">
		No messages found.
	</div>
</g:else>
