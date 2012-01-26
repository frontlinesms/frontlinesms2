<g:hiddenField name="sortField" value="${params.sort}"/>
<g:hiddenField name="sortOrder" value="${params.order}"/>
<g:hiddenField name="checkedMessageList" value=","/>
<g:hiddenField name="messageSection" value="${messageSection}"/>
<g:hiddenField name="ownerId" value="${ownerInstance?.id}"/>
<g:hiddenField name="messageTotal" value="${messageInstanceTotal}"/>
<g:set var="messageLabel" value="${(messageSection == 'sent' || messageSection == 'pending') ?
		message(code: 'fmessage.src.label', default: 'To')
			: message(code: 'fmessage.dst.label', default: 'From')}" />
<div id="messages" class="${(messageSection == 'radioShow') ? '' : 'tall-header'}">
	<table id="message-list" cellspacing=0>
		<thead>
			<tr id="message-sorter">
				<th class="message-select-cell">
					<g:checkBox name="message-select" class="message-select" id="message-select-all" value="0" checked="false" onclick="checkAll()"/></td>
				<th/>
				<th class="message-star-cell"></th>
		    		<g:sortableColumn class="message-preview-sender message-sender-cell" property="displayName" title="${messageLabel}"
						params="${params}" id='source-header' />
	    			<g:sortableColumn class="message-text-cell" property="text" title="${message(code: 'fmessage.text.label', default: 'Message')}" 
						params="${params}" id="message-header" />
					<g:sortableColumn class="message-date-cell" property="date" title="${message(code: 'fmessage.date.label', default: 'Date')}"
							params="${params}" id="timestamp-header" defaultOrder="desc" />
			</tr>
		</thead>
		<tbody>
			<g:if test="${messageInstanceTotal > 0}">
				<g:each in="${messageInstanceList}" status="i" var="m">
					<g:if test="${m instanceof frontlinesms2.Fmessage}">
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
									<g:if test="${messageSection == sent || messageSection == pending}">
										<g:formatDate format="dd MMMM, yyyy hh:mm a" date="${m.dateSent}" />
									</g:if>
									<g:else>
										<g:formatDate format="dd MMMM, yyyy hh:mm a" date="${m.date}" />
									</g:else>
								</g:link>
							</td>
						</tr>
					</g:if>
					<g:else>
						<tr class="message-list-separator">
							<td>
								${m}
							</td>
						</tr>
					</g:else>
				</g:each>
			</g:if>
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
	<g:render template="/message/message_details" plugin="core"/>
</div>
