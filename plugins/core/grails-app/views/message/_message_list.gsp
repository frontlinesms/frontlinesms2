<g:hiddenField name="sortField" value="${params.sort}"/>
<g:hiddenField name="messageTotal" value="${messageInstanceTotal}"/>
<g:if test="${messageSection == 'search'}">
  	<g:hiddenField name="activityId" value="${params.activityId}"/>
  	<g:hiddenField name="groupId" value="${params.groupId}"/>
  	<g:hiddenField name="searchString" value="${params.searchString}"/>
</g:if>
<table id="list">
	<thead>
		<tr>
			<th>
				<fsms:checkBox name="message-select" class="message-select" id="message-select-all" value="0" checked="false" onclick="checkAll('message')" disabled="${messageSection == 'trash'}"/>
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
		<g:if test="${messageInstanceTotal > 0}">
			<g:if test="${messageSection == 'trash' && !params.starred}">
				<fsms:render template="/message/trash_list"/>
			</g:if>
			<g:else>
				<g:each in="${messageInstanceList}" status="i" var="m">
					<tr class="message-preview ${m == messageInstance ? 'selected initial-selection' : ''} ${m.read?'read':'unread'}  ${m.hasFailed ? 'send-failed' : '' }" id="message-${m.id}">
						<g:hiddenField name="message-created-date" value="${m.date}" disabled="true"/>
						<td colspan="1" class="message-select-cell">
							<g:checkBox class="message-select message-select-checkbox" name="message-select" id="message-select-${m.id}" checked="${params.checkedId == m.id+'' ? 'true': 'false'}" value="${m.id}" onclick="itemCheckChanged('message', ${m.id});"/>
							<g:hiddenField name="src-${m.id}" value="${m.src}" disabled="true"/>
						</td>

						<td id="star-${m.id}" >
							<g:remoteLink class="${m.starred ? 'starred' : 'unstarred'}" controller="message" action="changeStarStatus" params='[messageId: "${m.id}"]' onSuccess="setStarStatus('star-${m.id}', data)"/>
						</td>
						<td class="message-sender-cell">
								<g:link class="displayName-${m.id}" controller="${params.controller}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})  + [messageId: m.id]}">
									<fsms:unbroken>${m.displayName}</fsms:unbroken>
								</g:link>
						</td>
						<td class="message-text-cell ${m.messageOwner ? (m.messageOwner instanceof frontlinesms2.Folder ? 'folderOwner' : 'activityOwner') : ''}">
							<g:link controller="${params.controller}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})  + [messageId: m.id]}">
								${m.displayText?.truncate(50)}
							</g:link>
						</td>
						<td class="message-date-cell">
							<g:link controller="${params.controller}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})   + [messageId: m.id]}">
								<fsms:unbroken>
									<g:formatDate format="dd MMMM, yyyy hh:mm a" date="${m.date}"/>
								</fsms:unbroken>
							</g:link>
						</td>
					</tr>
				</g:each>
			</g:else>
		</g:if>
		<g:elseif test="${(messageSection == 'result') && (searchDescription != 'null')}">
			<tr id="no-search-description">
				<td colspan="5">
					<h3><g:message code="fmessage.search.description"/></h3>
				</td>
			</tr>
		</g:elseif>
		<g:else>
			<tr id="no-messages">
				<td colspan="5">
					<h3><g:message code="fmessage.messages.none"/></h3>
				</td>
			</tr>
		</g:else>
	</tbody>
</table>

