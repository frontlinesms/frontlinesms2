<%@ page import="frontlinesms2.MessageStatus" %>
<g:hiddenField name="sortField" value="${params.sort}"/>
<g:hiddenField name="checkedMessageList" value=","/>
<g:hiddenField name="messageSection" value="${messageSection}"/>
<g:hiddenField name="ownerId" value="${ownerInstance?.id}"/>
<g:hiddenField name="messageTotal" value="${messageInstanceTotal}"/>
<g:set var="messageLabel" value="${(messageSection == 'sent' || messageSection == 'pending') ?
		message(code: 'fmessage.src.label', default: 'To')
			: message(code: 'fmessage.dst.label', default: 'From')}" />
<g:if test="${messageSection == 'search'}">
  	<g:hiddenField name="activityId" value="${params.activityId}"/>
  	<g:hiddenField name="groupId" value="${params.groupId}"/>
  	<g:hiddenField name="searchString" value="${params.searchString}"/>
</g:if>
<div id="messages" class="${(messageSection == 'inbox' || messageSection == 'sent' || messageSection == 'pending' || messageSection == 'trash' || messageSection == 'radioShow') ? '' : 'tall-header'}">
	<table id="message-list" cellspacing=0>
		<thead>
			<tr id="message-sorter">
				<th class="message-select-cell">
					<g:checkBox name="message-select" class="message-select" id="message-select-all" value="0" checked="false" onclick="checkAll()"/></td>
				<th/>
				<th class="message-star-cell"></th>
			    	<g:if test="${messageSection == 'trash'}">
			    		<g:sortableColumn class="message-preview-sender message-sender-cell" property="identifier" title="${messageLabel}"
							params="${params}" id='source-header' />
		    			<g:sortableColumn class="message-text-cell" property="message" title="${message(code: 'fmessage.text.label', default: 'Message')}" 
							params="${params}" id="message-header" />
						<g:sortableColumn class="message-date-cell" property="dateCreated" title="${message(code: 'fmessage.date.label', default: 'Date')}"
						params="${params}" id="timestamp-header" defaultOrder="desc" />
			    	</g:if>
			    	<g:else>
			    		<g:sortableColumn class="message-preview-sender message-sender-cell" property="contactName" title="${messageLabel}"
							params="${params}" id='source-header' />
		    			<g:sortableColumn class="message-text-cell" property="text" title="${message(code: 'fmessage.text.label', default: 'Message')}" 
							params="${params}" id="message-header" />
						<g:if test="${messageSection == sent || messageSection == pending}">
							<g:sortableColumn class="message-date-cell" property="dateSent" title="${message(code: 'fmessage.date.label', default: 'Date')}"
								params="${params}" id="timestamp-header" defaultOrder="desc" />
						</g:if>
						<g:else>
							<g:sortableColumn class="message-date-cell" property="dateReceived" title="${message(code: 'fmessage.date.label', default: 'Date')}"
								params="${params}" id="timestamp-header" defaultOrder="desc" />
						</g:else>
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
						<tr class="message-preview ${m == messageInstance ? 'selected' : ''} ${m.read?'read':'unread'}  ${m.status == MessageStatus.SEND_FAILED ? 'send-failed' : '' }" id="message-${m.id}">
							<td class="message-preview-select message-select-cell">
								<g:checkBox class="message-select message-select-checkbox" name="message-select" id="message-select-${m.id}" checked="${params.checkedId == m.id+'' ? 'true': 'false'}" value="${m.id}" onclick="messageChecked(${m.id});" />
								<g:hiddenField name="src-${m.id}" value="${m.src}"/>
							</td>

							<td class="message-preview-star message-star-cell" id="star-${m.id}" >
								<g:remoteLink class="${m.starred ? 'starred' : 'unstarred'}" controller="message" action="changeStarStatus" params='[messageId: "${m.id}"]' onSuccess="setStarStatus('star-${m.id}', data)"/>
							</td>
							<td class="message-preview-sender message-sender-cell">
									<g:link class="displayName-${m.id}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})  + [messageId: m.id, viewingArchive:viewingArchive]}">
										${m.contactName}
									</g:link>
							</td>
							<td class="message-text-cell">
								<g:link action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})  + [messageId: m.id, viewingArchive:viewingArchive]}">
									${m.displayText?.truncate(50)}
								</g:link>
							</td>
							<td class="message-date-cell">
								<g:link  action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})   + [messageId: m.id, viewingArchive:viewingArchive]}">
									<g:if test="${messageSection == sent || messageSection == pending}">
										<g:formatDate format="dd MMMM, yyyy hh:mm a" date="${m.dateSent}" />
									</g:if>
									<g:else>
										<g:formatDate format="dd MMMM, yyyy hh:mm a" date="${m.dateReceived}" />
									</g:else>
								</g:link>
							</td>
						</tr>
					</g:each>
				</g:else>
				</g:if>
				<g:elseif test="${(messageSection == 'result') && (searchDescription != 'null')}">
					<tr id="no-search-description">
						<td></td>
						<td></td>
						<td><h1>Start new search on the left</h1></td>
						<td></td>
						<td></td>
					</tr>
				</g:elseif>
				<g:else>
					<tr id="no-messages">
						<td></td>
						<td></td>
						<td>No messages</td>
						<td></td>
						<td></td>
					</tr>
				</g:else>
		</tbody>
	</table>
	<g:render template="../message/message_details" />
</div>
