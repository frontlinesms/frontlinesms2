<table>
	<tr>
		<td id="poll-details-cell">
			<g:formatDate date="${ownerInstance?.dateCreated}" />
			<span id="poll-sent">   (${g.message(code:'poll.messages.sent', args:[sentDispatchCount])})</span>
			<p>${ownerInstance?.sentMessageText}</p>
			<p>${ownerInstance.autoreplyText ? g.message(code:'poll.response.enabled') : ""}</p>
		</td>
		<td id="poll-stats">
			<table>
				<tbody>
					<g:each in="${ownerInstance.responses}" var="r">
						<tr>
							<td class='answers'>
								${r.value}
							</td>
							<td class='count'>
								${r.liveMessageCount}
							</td>
							<td class='percent'>
								<span>
									<g:formatNumber number="${ownerInstance.liveMessageCount - sentMessageCount != 0 ? r.liveMessageCount/(ownerInstance.liveMessageCount - sentMessageCount) : 0}" type="percent" maxFractionDigits="2" />
								</span>
							</td>
						</tr>
					</g:each>
				</tbody>
			</table>
		</td>
	</tr>
</table>
