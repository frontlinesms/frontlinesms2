<%@ page import="frontlinesms2.MessageStatus"%>
<g:each in="${trashInstanceList}" status="i" var="m">
	<g:if test="${m.objectType == 'frontlinesms2.Fmessage'}">
		<tr class="${m.link == messageInstance?'selected':''} ${m.link.read ? 'read':'unread'}  ${m.link.status == MessageStatus.SEND_FAILED ? 'send-failed' : '' }" id="message-${m.link.id}">
			<td>
				<g:checkBox class="message-select" name="message-select" id="message-select-${m.link.id}" checked="${params.checkedId == m.link.id+'' ? 'true': 'false'}" value="${m.id}" onclick="messageChecked(${m.link.id});" />
				<g:hiddenField name="src-${m.link.id}" value="${m.link.src}"/>
			</td>
			<td>
				<g:remoteLink controller="message" action="changeStarStatus" params='[messageId: "${m.link.id}"]' onSuccess="setStarStatus('star-${m.link.id}',data)">
					<div id="star-${m.link.id}" class="${m.link.starred? 'starred':'unstarred'}">
					</div>
				</g:remoteLink>
			</td>
			<td>
				<g:link class="displayName-${m.linkId}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					<g:if test="${m.link.status != null && m.link.status != MessageStatus.INBOUND}"><span>To:</span></g:if>${m.identifier}
				</g:link>
			</td>
			<td>
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					${m.message?.size() < 60 ? m.message : m.message?.substring(0,60) + "..."}
				</g:link>
			</td>
			<td>
				<g:link  action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					<g:formatDate format="dd MMMM, yyyy hh:mm a" date="${m.dateCreated}" />
				</g:link>
			</td>
		</tr>
	</g:if>
	<g:else>
		<tr class="${m.link == ownerInstance?'selected':''}" id="activity-${m.id}">
			<td>
				<g:checkBox disabled="true" name="message-select"/>
			</td>
			<td>
				<div id="star-${m.id}" class="unstarred">
				</div>
			</td>
			<td>
				<g:link class="displayName-${m.id}" action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					${m.identifier}
				</g:link>
			</td>
			<td>
				<g:link action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					${m.message == "1" ? "1 message" : m.message + " messages"}
				</g:link>
			</td>
			<td>
				<g:link  action="${messageSection}" params="${params.findAll({it.key != 'checkedId'}) + [id: m.id]}">
					<g:formatDate format="dd MMMM, yyyy hh:mm a" date="${m.dateCreated}" />
				</g:link>
			</td>
		</tr>
	</g:else>
</g:each>