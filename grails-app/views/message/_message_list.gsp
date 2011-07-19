  <%@ page contentType="text/html;charset=UTF-8" %>
<script type="text/javascript">
	function setStarStatus(object,data){
		if($("#"+object).hasClass("starred")) {
			$("#"+object).removeClass("starred");
		}
		
		$("#"+object).addClass(data);
		if(data != '') {
			$("#"+object).empty().append("Remove Star");
		} else {
			$("#"+object).empty().append("Add Star");
		}
	}
</script>
<g:if test="${messageInstanceTotal > 0}">
	<table id="messages">
		<thead>
			<tr>
				<td></td>
				<g:if test="${messageSection == 'sent' || messageSection == 'pending'}">
			    	<td><g:message code="fmessage.src.label" default="To"/></td>
			    </g:if>
			    <g:else>
			    	<td><g:message code="fmessage.src.label" default="From"/></td>
			    </g:else>
			    <td><g:message code="fmessage.text.label" default="Message"/></td>
			    <td><g:message code="fmessage.date.label" default="Date"/></td>
			</tr>
		</thead>
		<tbody>
			<g:each in="${messageInstanceList }" status="i" var="m">
				<tr class="${m == messageInstance?'selected':''} ${m.read?'read':'unread'} ${m.status}" id="message-${m.id}">
					<td>
					  <g:remoteLink controller="message" action="changeStarStatus" params='[messageId: "${m.id}"]' onSuccess="setStarStatus('star-${m.id}',data)">
							<div id="star-${m.id}" class="${m.starred? 'starred':''}">
								${m.starred?'Remove Star':'Add Star'}
							</div>
					  </g:remoteLink>
					</td>
					<td>
							<g:link action="${messageSection}" params="${params + [messageId: m.id]}">
								${m.displaySrc}
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
		<tfoot id="footer">
			<g:paginate next="Forward" prev="Back"
				 max="${grailsApplication.config.pagination.max}"
				action="${messageSection}" total="${messageInstanceTotal}" params= "${params.findAll({it.key != 'messageId'})}"/>
		</tfoot>
	</table>
</g:if>
<g:else>
	<div id="messages">
		No messages
	</div>
</g:else>
