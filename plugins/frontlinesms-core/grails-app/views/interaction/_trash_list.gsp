<%@ page import="frontlinesms2.*" %>
<g:each in="${trashInstanceList}" status="i" var="t">
	<g:if test="${t.objectClass == 'frontlinesms2.TextMessage'}">
		<g:hiddenField name="src-${t.object.id}" value="${t.object.src}"/>
		<tr class="message-preview ${t.object == interactionInstance ? 'selected' : ''}" id="message-${t.object.id}">
	</g:if>
	<g:else>
		<tr class="${t.object == ownerInstance ? 'selected' : ''}" id="activity-${t.id}">
	</g:else>
		<td class="interaction-select-cell">
			<g:checkBox disabled="${t.objectClass == frontlinesms2.TextMessage ? 'false' : 'true'}" class="interaction-select interaction-select-checkbox" name="interaction-select" id="interaction-select-${t.object.id}" checked="${params.checkedId == t.object.id+'' ? 'true': 'false'}" value="${t.id}" onclick="check_list.itemCheckChanged('interaction',${t.object.id}, 'message');"/>
		</td>
		<td class="message-star-cell" id="star-${t.objectId}">
			<g:if test="${t.objectClass == frontlinesms2.TextMessage}">
				<g:remoteLink class="${t.object.starred? 'starred':'unstarred'}" controller="message" action="changeStarStatus" params="[messageId: t.object.id]" onSuccess="setStarStatus('star-${t.object.id}',data)">
				</g:remoteLink>
			</g:if>
			<g:else>
				<div id="star-${t.id}" class="unstarred"/>
			</g:else>
		</td>
		<td class="message-sender-cell">
			<g:link class="displayName-${t.objectId}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: t.id]}">
				<g:if test="${t.objectClass == frontlinesms2.TextMessage && !t.object.inbound}">
					<span><g:message code="fmessage.to.label"/></span>
				</g:if>
				${t.displayName}
			</g:link>
		</td>
		<td class="message-text-cell">
			<g:link action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: t.id]}">
				${t.displayText?.truncate(50)}
			</g:link>
		</td>
		<td class="message-date-cell">
			<g:link  action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: t.id]}">
				<g:formatDate date="${t.dateCreated}"/>
			</g:link>
		</td>
	</tr>
</g:each>
