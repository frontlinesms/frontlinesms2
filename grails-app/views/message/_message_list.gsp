  <%@ page contentType="text/html;charset=UTF-8" %>
<script type="text/javascript">
	function setStarStatus(object,data){
		if($("#"+object).hasClass("starred")) {
			$("#"+object).removeClass("starred");
		}
		
		$("#"+object).addClass(data);
		if(data != '') {
			$("#"+object).empty().append("Remove Star");
		} else {
			$("#"+object).empty().append("Add Star");
		}
	}
</script>
<g:if test="${messageInstanceTotal > 0}">
	<table id="messages">
		<thead>
			<tr>
				<td></td>
				<g:if test="${messageSection == 'sent' || messageSection == 'pending'}">
			    	<td><g:message code="fmessage.src.label" default="To"/></td>
			    </g:if>
			    <g:else>
			    	<td><g:message code="fmessage.src.label" default="From"/></td>
			    </g:else>
			    <td><g:message code="fmessage.text.label" default="Message"/></td>
			    <td><g:message code="fmessage.date.label" default="Date"/></td>
			</tr>
		</thead>
		<tbody>
			<g:each in="${messageInstanceList }" status="i" var="m">
				<tr class="${m == messageInstance?'selected':''} ${m.read?'read':'unread'} ${m.status}" id="message-${m.id}">
					<td>
					  <g:remoteLink controller="message" action="changeStarStatus" params='[messageId: "${m.id}"]' onSuccess="setStarStatus('star-${m.id}',data)">
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
						<g:elseif test="${messageSection == 'search'}">
							<g:link controller="search" action="result" params="[activityId: activityId, groupId: groupInstance?.id, searchString: searchString, messageId: m.id]">
								${m.displaySrc}
							</g:link>
						</g:elseif>
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
						<g:elseif test="${messageSection == 'search'}">
							<g:link controller="search" action="result" params="[activityId: activityId, groupId: groupInstance?.id, searchString: searchString, messageId: m.id]">
								${m.displayText}
							</g:link>
						</g:elseif>
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
						<g:elseif test="${messageSection == 'search'}">
							<g:link controller="search" action="result" params="[activityId: activityId, groupId: groupInstance?.id, searchString: searchString, messageId: m.id]">
								<g:formatDate format="dd-MMM-yyyy hh:mm" date="${m.dateCreated}" />
							</g:link>
						</g:elseif>
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
		No messages
	</div>
</g:else>
