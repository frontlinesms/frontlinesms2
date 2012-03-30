<g:hiddenField name="sortField" value="${params.sort}"/>
<g:hiddenField name="checkedMessageList" value=","/>
<g:hiddenField name="messageSection" value="${messageSection}"/>
<g:hiddenField name="ownerId" value="${ownerInstance?.id}"/>
<g:hiddenField name="messageTotal" value="${messageInstanceTotal}"/>
<g:if test="${messageSection == 'search'}">
  	<g:hiddenField name="activityId" value="${params.activityId}"/>
  	<g:hiddenField name="groupId" value="${params.groupId}"/>
  	<g:hiddenField name="searchString" value="${params.searchString}"/>
</g:if>
<div id="messages" class="${(messageSection == 'inbox' || messageSection == 'sent' || messageSection == 'pending' || messageSection == 'trash' || messageSection == 'radioShow' || messageSection == 'folder' || params.action == 'no_search') ? '' : 'tall-header'}">
	<div id="message-list">
		<table cellspacing="0">
			<thead>
				<tr id="message-sorter">
					<th class="message-select-cell">
						<g:checkBox name="message-select" class="message-select" id="message-select-all" value="0" checked="false" onclick="checkAll()"/>
					</th>
					<th class="message-star-cell"></th>
			    	<g:if test="${messageSection == 'trash'}">
			    		<g:sortableColumn class="message-preview-sender message-sender-cell" property="identifier" title="${message(code: 'fmessage.displayName.label', default: 'Name')}"
								params="${params}" id='source-header' />
		    			<g:sortableColumn class="message-text-cell" property="message" title="${message(code: 'fmessage.text.label', default: 'Message')}" 
								params="${params}" id="message-header" />
						<g:sortableColumn class="message-date-cell" property="date" title="${message(code: 'fmessage.date.label', default: 'Date')}"
								params="${params}" id="timestamp-header" defaultOrder="desc" />
			    	</g:if>
			    	<g:else>
			    		<g:sortableColumn class="message-preview-sender message-sender-cell" property="displayName" title="${message(code: 'fmessage.displayName.label', default: 'Name')}"
								params="${params}" id='source-header' />
		    			<g:sortableColumn class="message-text-cell" property="text" title="${message(code: 'fmessage.text.label', default: 'Message')}" 
								params="${params}" id="message-header" />
						<g:sortableColumn class="message-date-cell" property="date" title="${message(code: 'fmessage.date.label', default: 'Date')}"
								params="${params}" id="timestamp-header" defaultOrder="desc" />
			    	</g:else>
				</tr>
			</thead>
			<tbody>
				<g:if test="${messageInstanceTotal > 0}">
					<g:if test="${messageSection == 'trash' && !params.starred}">
						<g:render template="../message/trash_list"></g:render>
					</g:if>
					<g:else>
						<g:each in="${messageInstanceList}" status="i" var="m">
							<tr class="message-preview ${m == messageInstance ? 'selected' : ''} ${m.read?'read':'unread'}  ${m.hasFailed ? 'send-failed' : '' }" id="message-${m.id}">
								<g:hiddenField name="message-created-date" value="${m.date}"/>
								<td colspan="1" class="message-select-cell">
									<g:checkBox class="message-select message-select-checkbox" name="message-select" id="message-select-${m.id}" checked="${params.checkedId == m.id+'' ? 'true': 'false'}" value="${m.id}" onclick="messageChecked(${m.id});" />
									<g:hiddenField name="src-${m.id}" value="${m.src}"/>
								</td>
	
								<td class="message-preview-star message-star-cell" id="star-${m.id}" >
									<g:remoteLink class="${m.starred ? 'starred' : 'unstarred'}" controller="message" action="changeStarStatus" params='[messageId: "${m.id}"]' onSuccess="setStarStatus('star-${m.id}', data)"/>
								</td>
								<td class="message-preview-sender message-sender-cell">
										<g:link class="displayName-${m.id}" controller="${params.controller}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})  + [messageId: m.id]}">
											${m.displayName}
										</g:link>
								</td>
								<td class="message-text-cell ${m.messageOwner ? (m.messageOwner instanceof frontlinesms2.Folder ? 'folderOwner' : 'activityOwner') : ''}">
									<g:link controller="${params.controller}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})  + [messageId: m.id]}">
										${m.displayText?.truncate(50)}
									</g:link>
								</td>
								<td class="message-date-cell">
									<g:link controller="${params.controller}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})   + [messageId: m.id]}">
										<g:formatDate format="dd MMMM, yyyy hh:mm a" date="${m.date}" />
									</g:link>
								</td>
							</tr>
						</g:each>
					</g:else>
					</g:if>
					<g:elseif test="${(messageSection == 'result') && (searchDescription != 'null')}">
						<tr id="no-search-description">
							<td colspan="5">
								<h1>Start new search on the left</h1>
							</td>
						</tr>
					</g:elseif>
					<g:else>
						<tr id="no-messages">
							<td colspan="5">
								<h1>No messages here!</h1>
							</td>
						</tr>
					</g:else>
			</tbody>
		</table>
	</div>
	<g:render template="../message/message_details" plugin="core"/>
</div>
