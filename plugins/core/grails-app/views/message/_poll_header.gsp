<h3 id="poll-title">${ownerInstance?.title} poll</h3>
<g:render template="../message/poll_buttons"/>
<div id="activity-details">
	<g:if test="$responseList">
		<table>
			<tr>
				<td>
					<g:formatDate date="${ownerInstance?.dateCreated}" /><span id="poll-sent">   (${sentMessageCount} messages sent)</span>
					<p>${ownerInstance?.messageText}</p>
					<p>${ownerInstance.autoReplyText ? "Auto Response Enabled" : ""}</p>
				</td>
				<td>
					<table id="poll-stats">
						<tbody>
							<g:each in="${responseList}" var="r">
								<tr>
									<td class='answers'>
										${r.value}
									</td>
									<td class='count'>
										${r.count}
									</td>
									<td class='percent'>
										(${r.percent}%)
									</td>
								</tr>
							</g:each>
						</tbody>
					</table>
				</td>
			</tr>
		</table>
	</g:if>
</div>
