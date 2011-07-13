  <%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${messageInstanceTotal > 0}">
	<table id="messages">
		<thead>
			<tr>
				<td><g:checkBox name="message" value="0" checked="false" onclick="checkAllMessages()"/></td>
			    <td><g:message code="fmessage.src.label" default="Name"/></td>
			    <td><g:message code="fmessage.text.label" default="Snippet"/></td>
			    <td><g:message code="fmessage.date.label" default="Date"/></td>
			</tr>
		</thead>
		<tbody>
			<g:each in="${messageInstanceList }" status="i" var="m">
				<tr class="${m == messageInstance?'selected':''} ${m.read?'read':'unread'} ${m.status}" id="message-${m.id}">
					<td><g:checkBox name="message" checked="false" value="${messageInstance.id}" onclick="appendMessageDetails()"/></td>
					<td>
					  <g:remoteLink action="changeStarStatus" params='[messageId: "${m.id}"]' onSuccess="setStarStatus('star-${m.id}',data)">
							<div id="star-${m.id}" class="${m.starred? 'starred':''}">
								${m.starred?'Remove Star':'Add Star'}
							</div>
					  </g:remoteLink>
					</td>
					<td>
						<g:if test="${ownerInstance}">
							<g:link action="${messageSection}" params="[messageId: m.id, ownerId: ownerInstance.id]">
								${m.displaySrc}
							</g:link>
						</g:if>
						<g:else>
							<g:link action="${messageSection}" params="[messageId: m.id]">
								${m.displaySrc}
							</g:link>
						</g:else>
					</td>
					<td>
						<g:if test="${ownerInstance}">
							<g:link action="${messageSection}" params="[messageId: m.id, ownerId: ownerInstance.id]">
								${m.displayText}
							</g:link>
						</g:if>
						<g:else>
							<g:link action="${messageSection}" params="[messageId: m.id]">
							  ${m.displayText}
							</g:link>
						</g:else>
					</td>
					<td>
						<g:if test="${ownerInstance}">
							<g:link action="${messageSection}" params="[messageId: m.id, ownerId:ownerInstance.id]">
								<g:formatDate format="dd-MMM-yyyy hh:mm" date="${m.dateCreated}" />
							</g:link>
						</g:if>
						<g:else>
							<g:link  action="${messageSection}" params="[messageId: m.id]">
								<g:formatDate format="dd-MMM-yyyy hh:mm" date="${m.dateCreated}" />
							</g:link>
						</g:else>
					</td>
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
