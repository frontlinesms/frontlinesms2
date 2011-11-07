<%@ page import="frontlinesms2.MessageStatus"%>
<div id="message-list">
	<g:hiddenField name="sortField" value="${params.sort}"/>
	<g:hiddenField name="checkedMessageList" value=","/>
	<g:hiddenField name="messageSection" value="${messageSection}"/>
	<g:hiddenField name="ownerId" value="${ownerInstance?.id}"/>
	<g:hiddenField name="messageTotal" value="${messageInstanceTotal}"/>
	<g:if test="${messageInstanceTotal > 0}">
		<g:hiddenField name="viewingArchive" value="${params.viewingArchive}"/>
		<g:set var="messageLabel" value="${(messageSection == 'sent' || messageSection == 'pending') ?
				message(code: 'fmessage.src.label', default: 'To')
	 			: message(code: 'fmessage.dst.label', default: 'From')}" />
		<g:if test="${messageSection == 'search'}">
		  	<g:hiddenField name="activityId" value="${params.activityId}"/>
		  	<g:hiddenField name="groupId" value="${params.groupId}"/>
		  	<g:hiddenField name="searchString" value="${params.searchString}"/>
		</g:if>
		<table id="messages">
			<thead>
				<tr>
					<td>
						<g:checkBox name="message-select" class="message-select" id="message-select-all" value="0" checked="false" onclick="checkAll()"/></td>
					<td />
				    	<g:if test="${messageSection == 'trash'}">
				    		<g:sortableColumn property="identifier" title="${messageLabel}"
								params="${params}" id='source-header' />
			    			<g:sortableColumn property="message" title="${message(code: 'fmessage.text.label', default: 'Message')}" 
								params="${params}" id="message-header" />
							<g:sortableColumn property="dateCreated" title="${message(code: 'fmessage.date.label', default: 'Date')}"
							params="${params}" id="timestamp-header" defaultOrder="desc" />
				    	</g:if>
				    	<g:else>
				    		<g:sortableColumn property="contactName" title="${messageLabel}"
								params="${params}" id='source-header' />
			    			<g:sortableColumn property="text" title="${message(code: 'fmessage.text.label', default: 'Message')}" 
								params="${params}" id="message-header" />
							<g:if test="${messageSection == sent || messageSection == pending}">
								<g:sortableColumn property="dateSent" title="${message(code: 'fmessage.date.label', default: 'Date')}"
									params="${params}" id="timestamp-header" defaultOrder="desc" />
							</g:if>
							<g:else>
								<g:sortableColumn property="dateReceived" title="${message(code: 'fmessage.date.label', default: 'Date')}"
									params="${params}" id="timestamp-header" defaultOrder="desc" />
							</g:else>
				    	</g:else>
				</tr>
			</thead>
			<tbody id='messages-table'>
				<g:if test="${messageSection == 'trash' && !params.starred}">
					<g:render template="../message/trash_list"></g:render>
				</g:if>
				<g:else>
					<g:each in="${messageInstanceList}" status="i" var="m">
						<tr class="${m == messageInstance?'selected':''} ${m.read?'read':'unread'}  ${m.status == MessageStatus.SEND_FAILED ? 'send-failed' : '' }" id="message-${m.id}">
							<td>
								<g:checkBox class="message-select" name="message-select" id="message-select-${m.id}" checked="${params.checkedId == m.id+'' ? 'true': 'false'}" value="${m.id}" onclick="messageChecked(${m.id});" />
								<g:hiddenField name="src-${m.id}" value="${m.src}"/>
							</td>
							<td>
								<g:remoteLink controller="message" action="changeStarStatus" params='[messageId: "${m.id}"]' onSuccess="setStarStatus('star-${m.id}',data)">
									<div id="star-${m.id}" class="${m.starred? 'starred':'unstarred'}">
									</div>
								</g:remoteLink>
							</td>
							<td>
								<g:link class="displayName-${m.id}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})  + [messageId: m.id]}">
									${m.contactName}
								</g:link>
							</td>
							<td>
								<g:link action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})  + [messageId: m.id]}">
									${m.displayText?.size() < 60 ? m.displayText : m.displayText?.substring(0,60) + "..."}
								</g:link>
							</td>
							<td>
								<g:link  action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})   + [messageId: m.id]}">
									<g:if test="${messageSection == sent || messageSection == pending}">
										<g:formatDate date="${m.dateSent}" />
									</g:if>
									<g:else>
										<g:formatDate date="${m.dateReceived}" />
									</g:else>
								</g:link>
							</td>
						</tr>
					</g:each>
				</g:else>
			</tbody>
		</table>
	</g:if>
	<g:elseif test="${(messageSection == 'result') && (searchDescription != 'null')}">
		<div id="no-search-description">
			<h1>Start new search on the left</h1>
		</div>
	</g:elseif>
	<g:else>
		<div id="no-messages">
			No messages
		</div>
	</g:else>
</div>
