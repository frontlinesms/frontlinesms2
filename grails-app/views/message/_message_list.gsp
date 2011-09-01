<%@ page import="frontlinesms2.enums.MessageStatus" %>
<div id="message-list">
	<g:hiddenField name="checkedMessageList" value=","/>
	<g:if test="${messageInstanceTotal > 0}">
		<g:hiddenField name="messageSection" value="${messageSection}"/>
		<g:hiddenField name="ownerId" value="${ownerInstance?.id}"/>
		<g:hiddenField name="isArchived" value="${params.archived}"/>
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
					<td><g:checkBox name="message" value="0" checked="false" onclick="checkAll()"/></td>
					<td />
				    	<g:sortableColumn property="contactName" title="${messageLabel}"
									params='[ownerId: "${ownerInstance?.id}"]' id='source-header' />
		    			<g:sortableColumn property="text" title="${message(code: 'fmessage.text.label', default: 'Message')}" 
									params='[ownerId: "${ownerInstance?.id}"]' id="message-header" />
						<g:sortableColumn property="dateCreated" title="${message(code: 'fmessage.date.label', default: 'Date')}"
									params='[ownerId: "${ownerInstance?.id}"]' id="timestamp-header" defaultOrder="desc" />

			</tr>
		</thead>
		<tbody id='messages-table'>
			<g:each in="${messageInstanceList}" status="i" var="m">
				<tr class="${m == messageInstance?'selected':''} ${m.read?'read':'unread'}  ${m.status == MessageStatus.SEND_FAILED ? 'send-failed' : '' }" id="message-${m.id}">
					<td>
						<g:checkBox class='checkbox' name="message" checked="${params.checkedId == m.id+'' ? 'true': 'false'}" value="${m.id}" onclick="messageChecked(${m.id});" />
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
							  ${m.displayText}
							</g:link>
					</td>
					<td>
							<g:link  action="${messageSection}" params="${params.findAll({it.key != 'checkedId'})   + [messageId: m.id]}">
								<g:formatDate date="${m.dateCreated}" />
							</g:link>
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>
	</g:if>
	<g:elseif test="${(messageSection == 'search') && (searchDescription != 'null')}">
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
