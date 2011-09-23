<div>
	<g:if test="${params.viewingArchive && params.viewingMessages}">
		<g:link controller="archive" action="poll"> &lt; Back </g:link>
	</g:if>
	<g:else>
		<img src='${resource(dir:'images/icons',file:'activities.png')}' />
	</g:else>
	<h2>${ownerInstance?.title}</h2>
	<div>${ownerInstance?.question}</div>
	<g:if test="$responseList">
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
	</g:if>
</div>
