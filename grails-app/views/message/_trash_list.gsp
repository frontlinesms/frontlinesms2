<%@ page import="frontlinesms2.MessageStatus"%>
<g:each in="${trashInstanceList}" status="i" var="m">
	<g:if test="${m instanceof frontlinesms2.Fmessage}">
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
				<g:link class="displayName-${m.id}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [type: m.class.name] + [id: m.id]}">
					${m.contactName}
				</g:link>
			</td>
			<td>
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [type: m.class.name] + [id: m.id]}">
					${m.displayText}
				</g:link>
			</td>
			<td>
				<g:link  action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [type: m.class.name] + [id: m.id]}">
					<g:formatDate date="${m.dateCreated}" />
				</g:link>
			</td>
		</tr>
	</g:if>
	<g:else>
		<tr class="${m == ownerInstance?'selected':''}" id="${m.class}-${m.id}">
			<td>
				<g:checkBox disabled="true" name="message-select"/>
			</td>
			<td>
				<div id="star-${m.id}" class="unstarred">
				</div>
			</td>
			<td>
				<g:link class="displayName-${m.id}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [type: m.class.name] + [id: m.id]}">
					${m instanceof frontlinesms2.Poll ? m.title : m.name}
				</g:link>
			</td>
			<td>
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [type: m.class.name] + [id: m.id]}">
					${m.getLiveMessageCount() == 1 ? "1 message" : m.getLiveMessageCount() + " messages"}
				</g:link>
			</td>
			<td>
				<g:link  action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [type: m.class.name] + [id: m.id]}">
					<g:formatDate date="${m.lastUpdated}" />
				</g:link>
			</td>
		</tr>
	</g:else>
</g:each>