<%@ page import="frontlinesms2.*" %>
<g:each in="${trashInstanceList}" status="i" var="m">
	<g:if test="${m.objectClass == frontlinesms2.Fmessage}">
		<tr class="${m.object == messageInstance ? 'selected' : ''} ${m.object.read ? 'read' : 'unread'}" id="message-${m.object.id}">
			<td class="message-select-cell">
				<g:checkBox class="message-select" name="message-select" id="message-select-${m.object.id}" checked="${params.checkedId == m.object.id+'' ? 'true': 'false'}" value="${m.id}" onclick="messageChecked(${m.object.id});" />
				<g:hiddenField name="src-${m.object.id}" value="${m.object.src}"/>
			</td>
			<td class="message-preview-star message-star-cell" >
				<g:remoteLink controller="message" action="changeStarStatus" params='[messageId: "${m.object.id}"]' onSuccess="setStarStatus('star-${m.object.id}',data)">
					<div id="star-${m.object.id}" class="${m.object.starred? 'starred':'unstarred'}">
					</div>
				</g:remoteLink>
			</td>
			<td class="message-preview-sender message-sender-cell">
				<g:link class="displayName-${m.objectId}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					<g:if test="${!m.object.inbound}"><span><g:message code="fmessage.to.label" /></span></g:if>${m.displayName}
				</g:link>
			</td>
			<td class="message-text-cell">
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					${m.displayDetail?.size() < 60 ? m.displayDetail : m.displayDetail?.substring(0,60) + "..."}
				</g:link>
			</td>
			<td class="message-date-cell">
				<g:link  action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					<g:formatDate format="dd MMMM, yyyy hh:mm a" date="${m.dateCreated}" />
				</g:link>
			</td>
		</tr>
	</g:if>
	<g:else>
		<tr class="${m.object == ownerInstance ? 'selected' : ''}" id="activity-${m.id}">
			<td class="message-select-cell">
				<g:checkBox disabled="true" name="message-select"/>
			</td>
			<td class="message-star-cell" >
				<div id="star-${m.id}" class="unstarred">
				</div>
			</td>
			<td class="message-sender-cell">
				<g:link class="displayName-${m.id}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					${m.displayName}
				</g:link>
			</td>
			<td class="message-text-cell">
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					${m.object.getLiveMessageCount() == 1 ? g.message(code:'fmessage.count') : m.object.getLiveMessageCount() + " " + g.message(code:'fmessage.many')}
				</g:link>
			</td>
			<td class="message-date-cell">
				<g:link  action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					<g:formatDate format="dd MMMM, yyyy hh:mm a" date="${m.dateCreated}" />
				</g:link>
			</td>
		</tr>
	</g:else>
</g:each>
