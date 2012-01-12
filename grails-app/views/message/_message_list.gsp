<g:hiddenField name="sortField" value="${params.sort}"/>
<g:hiddenField name="sortOrder" value="${params.order}"/>
<g:hiddenField name="checkedMessageList" value=","/>
<g:hiddenField name="messageSection" value="${messageSection}"/>
<g:hiddenField name="ownerId" value="${ownerInstance?.id}"/>
<g:hiddenField name="messageTotal" value="${messageInstanceTotal}"/>
<g:if test="${messageSection == 'search'}">
  	<g:hiddenField name="activityId" value="${params.activityId}"/>
  	<g:hiddenField name="groupId" value="${params.groupId}"/>
  	<g:hiddenField name="searchString" value="${params.searchString}"/>
</g:if>
<div id="messages" class="${(messageSection == 'inbox' || messageSection == 'sent' || messageSection == 'pending' || messageSection == 'trash' ||  messageSection == 'folder' || messageSection == 'radioShow') ? '' : 'tall-header'}">
	<table id="message-list" cellspacing=0>
		<thead>
			<tr id="message-sorter">
				<th class="message-select-cell">
					<g:checkBox name="message-select" class="message-select" id="message-select-all" value="0" checked="false" onclick="checkAll()"/></td>
				<th/>
				<th class="message-star-cell"></th>
			    	<g:if test="${messageSection == 'trash'}">
			    		<g:sortableColumn class="message-preview-sender message-sender-cell" property="identifier" title="${message(code: 'fmessage.displayName.label', default: 'Contact')}"
							params="${params}" id='source-header' />
		    			<g:sortableColumn class="message-text-cell" property="message" title="${message(code: 'fmessage.text.label', default: 'Message')}" 
							params="${params}" id="message-header" />
						<g:sortableColumn class="message-date-cell" property="dateCreated" title="${message(code: 'fmessage.date.label', default: 'Date')}"
							params="${params}" id="timestamp-header" defaultOrder="desc" />
			    	</g:if>
			    	<g:else>
			    		<g:sortableColumn class="message-preview-sender message-sender-cell" property="displayName" title="${message(code: 'fmessage.displayName.label', default: 'Contact')}"
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
							<td class="message-select-cell">
								<g:checkBox class="message-select message-select-checkbox" name="message-select" id="message-select-${m.id}" checked="${params.checkedId == m.id+'' ? 'true': 'false'}" value="${m.id}" onclick="messageChecked(${m.id});" />
								<g:hiddenField name="src-${m.id}" value="${m.src}"/>
							</td>

							<td class="message-preview-star message-star-cell" id="star-${m.id}" >
								<g:remoteLink class="${m.starred ? 'starred' : 'unstarred'}" controller="message" action="changeStarStatus" params='[messageId: "${m.id}"]' onSuccess="setStarStatus('star-${m.id}', data)"/>
							</td>
							<td class="message-preview-sender message-sender-cell">
									<g:link class="displayName-${m.id}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})  + [messageId: m.id, viewingArchive:viewingArchive]}">
										${m.displayName}
									</g:link>
							</td>
							<td class="message-text-cell">
								<g:link action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})  + [messageId: m.id, viewingArchive:viewingArchive]}">
									${m.displayText?.truncate(50)}
								</g:link>
							</td>
							<td class="message-date-cell">
								<g:link  action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})   + [messageId: m.id, viewingArchive:viewingArchive]}">
									<g:formatDate format="dd MMMM, yyyy hh:mm a" date="${m.date}" />
								</g:link>
							</td>
						</tr>
					</g:each>
				</g:else>
				</g:if>
				<g:elseif test="${(messageSection == 'result') && !search}">
					<h1 id="no-search-description">
						Start new search on the left
					</h1>
				</g:elseif>
				<g:elseif test="${(messageSection == 'result') && search && messageInstanceTotal == 0}">
					<h1 id="no-messages-search">
						No messages found! Try a new search on the left
					</h1>
				</g:elseif>
				<g:else>
					<h1 id="no-messages">
						No messages
					</h1>
				</g:else>
		</tbody>
	</table>
	<g:render template="../message/message_details" />
</div>
