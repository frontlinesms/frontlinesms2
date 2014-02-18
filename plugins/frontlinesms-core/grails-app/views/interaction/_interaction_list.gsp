<%@page defaultCodec="html" %>
<g:hiddenField name="sortField" value="${params.sort}"/>
<g:hiddenField name="messageTotal" value="${interactionInstanceTotal}"/>
<g:if test="${messageSection == 'search'}">
  	<g:hiddenField name="activityId" value="${params.activityId}"/>
  	<g:hiddenField name="groupId" value="${params.groupId}"/>
  	<g:hiddenField name="searchString" value="${params.searchString}"/>
</g:if>
<table id="main-list">
	<thead>
		<tr>
			<th>
				<fsms:checkBox name="message-select" class="message-select" id="message-select-all" value="0" checked="false" onclick="check_list.checkAll('interaction')" disabled="${messageSection == 'trash'}"/>
			</th>
			<th></th>
			<g:sortableColumn property="inboundContactName" title="${message(code:'fmessage.displayName.label')}" params="${params}" id='source-header'/>
			<g:sortableColumn class="content" property="text" title="${message(code:'fmessage.text.label')}"
					params="${params}" id="message-header"/>
			<g:sortableColumn property="date" title="${message(code:'fmessage.date.label')}"
					params="${params}" id="timestamp-header" defaultOrder="desc"/>
		</tr>
	</thead>
	<tbody>
		<g:if test="${interactionInstanceTotal > 0}">
			<g:if test="${messageSection == 'trash' && !params.starred}">
				<fsms:render template="/interaction/trash_list"/>
			</g:if>
			<g:else>

				<g:each in="${interactionInstanceList}" status="i" var="m">
					<tr class="message-preview ${m == interactionInstance ? 'selected initial-selection' : ''} ${m.read?'read':'unread'} ${m.archived?'archived':''} ${m.hasSent? 'sent':''} ${m.hasPending? 'pending':''} ${m.hasFailed? 'failed':''} ownerdetail-${m.messageOwner?.shortName}-${m.ownerDetail}" id="message-${m.id}">
						<td colspan="1" class="message-select-cell">
							<g:checkBox class="message-select message-select-checkbox" name="message-select" id="message-select-${m.id}" checked="${params.checkedId == m.id+'' ? 'true': 'false'}" value="${m.id}" onclick="check_list.itemCheckChanged('interaction', ${m.id});"/>
							<g:hiddenField name="src-${m.id}" value="${m.src}" disabled="true"/>
						</td>

						<td id="star-${m.id}" >
							<g:remoteLink class="${m.starred ? 'starred' : 'unstarred'}" controller="message" action="changeStarStatus" params='[messageId: "${m.id}"]' onSuccess="setStarStatus('star-${m.id}', data)"/>
						</td>
						<td class="message-sender-cell ${m.messageOwner ? (m.messageOwner instanceof frontlinesms2.Folder ? 'folderOwner' : 'activityOwner') : ''}">
								<g:link class="displayName-${m.id}" controller="${params.controller}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})  + [messageId: m.id]}">
									<g:if test="${m.inbound}">
										${m.displayName}
									</g:if>
									<g:elseif test="${m.dispatches.size() == 1}">
										<g:message code="fmessage.to" args="${[] << m.displayName}" />
									</g:elseif>
									<g:else>
										<g:message code="fmessage.to.multiple" args="${[] << m.displayName}" />
									</g:else>
								</g:link>
						</td>
						<td class="message-text-cell ${m.messageOwner ? (m.messageOwner instanceof frontlinesms2.Folder ? 'folderOwner' : 'activityOwner') : ''}">
							<g:link controller="${params.controller}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})  + [messageId: m.id]}">
								${m.messageOwner? m.messageOwner.getDisplayText(m).truncate(50) : m.text.truncate(50) }
							</g:link>
						</td>
						<td class="message-date-cell">
							<g:hiddenField name="message-created-date" value="${m.date}" disabled="true"/>
							<g:link controller="${params.controller}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})   + [messageId: m.id]}">
								<g:formatDate date="${m.date}"/>
							</g:link>
						</td>
					</tr>
				</g:each>
			</g:else>
		</g:if>
		<g:else>
			<tr class="no-content">
				<td colspan="5">
					<g:if test="${messageSection=='result'}">
						<g:if test="${searchDescription}">
							<g:message code="fmessage.search.none"/>
						</g:if>
						<g:else>
							<g:message code="fmessage.search.description"/>
						</g:else>
					</g:if>
					<g:elseif test="${(messageSection=='pending') || (messageSection=='sent')}">
						<g:message code="fmessage.messages.sent.none"/>
					</g:elseif>
					<g:else>
						<g:message code="fmessage.messages.none"/>
					</g:else>
				</td>
			</tr>
		</g:else>
	</tbody>
</table>

