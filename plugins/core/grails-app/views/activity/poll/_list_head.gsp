<ul class="info">
	<h1><g:message code="poll.title" args="${[ownerInstance.name]}"/></h1>
	<li><g:formatDate date="${ownerInstance?.dateCreated}"/></li>
	<li>(<g:message code="poll.messages.sent" args="${[sentDispatchCount]}"/>)</li>
	<li>${ownerInstance?.sentMessageText}</li>
	<li>${ownerInstance.autoreplyText? g.message(code:'poll.response.enabled') : ""}</li>
</ul>
<div class="stats">
	<table>
		<g:each in="${ownerInstance.responses}" var="r">
			<tr id="response-${r.id}">
				<td class="value">${r.value}</td>
				<td class="count">${r.liveMessageCount}</td>
				<td class="percent">
					<g:formatNumber number="${ownerInstance.liveMessageCount - sentMessageCount != 0 ? r.liveMessageCount/(ownerInstance.liveMessageCount - sentMessageCount) : 0}" type="percent" maxFractionDigits="2"/>
				</td>
			</tr>
		</g:each>
	</table>
	<a id='poll-graph-btn' class='btn show-arrow'><g:message code="fmessage.showpolldetails"/></a>
</div>

