<div id="message-title">
	<h2>${ownerInstance?.title}</h2>
	<g:if test="$responseList">
		<table id="poll-stats">
			<tbody>
				<g:each in="${responseList}" var="r">
					<tr>
						<td>
							${r.value}
						</td>
						<td>
							${r.count}
						</td>
						<td>
							(${r.percent}%)
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>
	</g:if>
</div>
