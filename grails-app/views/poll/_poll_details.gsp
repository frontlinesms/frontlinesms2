<h2>${ownerInstance?.title} poll ${ownerInstance.archived ? "(Archived)" : ""}</h2>
<table>
	<g:if test="$responseList">
		<tr>
			<td>
				<div><g:formatDate date="${ownerInstance?.dateCreated}" /><span> (${ownerInstance?.sentMessageCount} messages sent)</span></div>
				<div>
					${ownerInstance?.messageText} 
					.&nbsp;
					${ownerInstance.autoReplyText ? "Auto Reponse Enabled" : ""}
				</div>
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
	</g:if>
</table>
