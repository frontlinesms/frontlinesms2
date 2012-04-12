<g:each in="${trashInstanceList}" status="i" var="m">
	<g:if test="${m.objectType == 'frontlinesms2.Fmessage'}">
		<tr class="${m.link == messageInstance ? 'selected' : ''} ${m.link.read ? 'read' : 'unread'}" id="message-${m.link.id}">
			<td class="message-select-cell">
				<g:checkBox class="message-select" name="message-select" id="message-select-${m.link.id}" checked="${params.checkedId == m.link.id+'' ? 'true': 'false'}" value="${m.id}" onclick="messageChecked(${m.link.id});" />
				<g:hiddenField name="src-${m.link.id}" value="${m.link.src}"/>
			</td>
			<td class="message-preview-star message-star-cell" >
				<g:remoteLink controller="message" action="changeStarStatus" params='[messageId: "${m.link.id}"]' onSuccess="setStarStatus('star-${m.link.id}',data)">
					<div id="star-${m.link.id}" class="${m.link.starred? 'starred':'unstarred'}">
					</div>
				</g:remoteLink>
			</td>
			<td class="message-preview-sender message-sender-cell">
				<g:link class="displayName-${m.linkId}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					<g:if test="${!m.link.inbound}"><span><g:message code="fmessage.to.label" /></span></g:if>${m.identifier}
				</g:link>
			</td>
			<td class="message-text-cell">
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					${m.message?.size() < 60 ? m.message : m.message?.substring(0,60) + "..."}
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
		<tr class="${m.link == ownerInstance ? 'selected' : ''}" id="activity-${m.id}">
			<td class="message-select-cell">
				<g:checkBox disabled="true" name="message-select"/>
			</td>
			<td class="message-preview-star message-star-cell" >
				<div id="star-${m.id}" class="unstarred">
				</div>
			</td>
			<td class="message-preview-sender message-sender-cell">
				<g:link class="displayName-${m.id}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					${m.identifier}
				</g:link>
			</td>
			<td class="message-text-cell">
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					${m.message == "1" ? g.message(code:'fmessage.count') : g.message(code:'fmessage.count', params:[m.message])}
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