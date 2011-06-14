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
				<tr class="${m == messageInstance?'selected':''} ${m.read?'read':'unread'}" id="message-${m.id}">
					<td>
							<g:link action="show" id="${m.id}">
								${m.displaySrc}
							</g:link>
					</td>
					<td>
							<g:link action="show" id="${m.id}">
							  ${m.displayText}
							</g:link>
					</td>
					<td>
							<g:link action="show" id="${m.id}">
								<g:formatDate format="dd-MMM-yyyy hh:mm" date="${m.dateCreated}" />
							</g:link>
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
