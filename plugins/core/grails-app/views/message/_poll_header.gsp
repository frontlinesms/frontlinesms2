<h3 id="poll-title">${ownerInstance?.name} poll</h3>
<g:render template="../message/activity_buttons"/>
<div id="activity-details">
	<table>
		<tr>
			<td>
				<g:formatDate date="${ownerInstance?.dateCreated}" /><span id="poll-sent">   (${sentDispatchCount} messages sent)</span>
				<p>${ownerInstance?.sentMessageText}</p>
				<p>${ownerInstance.autoReplyText ? "Auto Response Enabled" : ""}</p>
			</td>
			<td>
				<table id="poll-stats">
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
</div>
