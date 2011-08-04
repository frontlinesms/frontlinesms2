  <%@ page contentType="text/html;charset=UTF-8" %>
<div id="export_button">
	<g:remoteLink controller="export" action="wizard" params='[messageSection: "${messageSection}", ownerId: "${ownerInstance?.id}", activityId: "${activityId}", searchString: "${searchString}", groupId: "${groupInstance?.id}"]' onSuccess="launchWizard('Export', data);">
		Export
	</g:remoteLink>		
</div>
<g:if test="${messageInstanceTotal > 0}">
	<g:hiddenField name="messageSection" value="${messageSection}"/>
	<g:hiddenField name="ownerId" value="${ownerInstance?.id}"/>
	<g:set var="messageLabel" value="${(messageSection == 'sent' || messageSection == 'pending') ? 
				message(code: 'fmessage.src.label', default: 'To')
	 			: message(code: 'fmessage.dst.label', default: 'From')}" />
	
	<table id="messages">
		<thead>
			<tr>
				<td><g:checkBox name="message" value="0" disabled="${messageSection == 'trash' ? 'true': 'false'}" checked="false" onclick="checkAllMessages()"/></td>
				<td />
		    	<g:sortableColumn property="contactName" title="${messageLabel}"
									params='[ownerId: "${ownerInstance?.id}"]' />
		    	<g:sortableColumn property="text" title="${message(code: 'fmessage.text.label', default: 'Message')}" 
									params='[ownerId: "${ownerInstance?.id}"]' />
				<g:sortableColumn property="dateCreated" title="${message(code: 'fmessage.date.label', default: 'Date')}"
									params='[ownerId: "${ownerInstance?.id}"]' />
			</tr>
		</thead>
		<tbody>
			<g:each in="${messageInstanceList}" status="i" var="m">
				<tr class="${m == messageInstance?'selected':''} ${m.read?'read':'unread'} ${m.status}" id="message-${m.id}">
					<td>
						<g:checkBox name="message" checked="${params.checkedId == m.id+'' ? 'true': 'false'}" value="${m.id}" onclick="updateMessageDetails(${m.id});" disabled="${messageSection == 'trash' ? 'true': 'false'}"/>
						<g:hiddenField name="src-${m.id}" value="${m.src}"/>
					</td>

					<td>
					  <g:remoteLink controller="message" action="changeStarStatus" params='[messageId: "${m.id}"]' onSuccess="setStarStatus('star-${m.id}',data)">
							<div id="star-${m.id}" class="${m.starred? 'starred':''}">
								${m.starred?'Remove Star':'Add Star'}
							</div>
					  </g:remoteLink>
					</td>
					<td>
							<g:link action="${messageSection}" params="${params + [messageId: m.id]}">
								${m.displayName}
							</g:link>
					</td>
					<td>
							<g:link action="${messageSection}" params="${params + [messageId: m.id]}">
							  ${m.displayText}
							</g:link>
					</td>
					<td>
							<g:link  action="${messageSection}" params="${params + [messageId: m.id]}">
								<g:formatDate format="dd-MMM-yyyy hh:mm" date="${m.dateCreated}" />
							</g:link>
					</td>
				</tr>
			</g:each>
		</tbody>
		<tfoot>
			<div id="footer">
				Show:
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset'}) + [starred: true]}" >Starred</g:link>
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'starred' && it.key != 'max' && it.key != 'offset'})}">All</g:link>

				
				<g:paginate next="Forward" prev="Back"
					 max="${grailsApplication.config.pagination.max}"
					action="${messageSection}" total="${messageInstanceTotal}" params= "${params.findAll({it.key != 'messageId'})}"/>
			</div>
		</tfoot>
	</table>
</g:if>
<g:else>
	<div id="messages">
		No messages
	</div>
</g:else>
