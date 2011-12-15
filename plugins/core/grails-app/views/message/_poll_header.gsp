<h3>${ownerInstance?.title} poll</h3><span id="poll-sent">(${ownerInstance?.sentMessageCount} messages sent)</span>
<g:render template="../message/poll_buttons"/>
<div id="activity-details">
	<g:if test="$responseList">
		<table>
			<tr>
				<td>
					<g:formatDate date="${ownerInstance?.dateCreated}" />
					<p>${ownerInstance?.messageText}</p>
					<p>${ownerInstance.autoReplyText ? "Auto Reponse Enabled" : ""}</p>
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
