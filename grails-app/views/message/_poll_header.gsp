<div>
	<img src='${resource(dir:'images/icons',file:'activities.gif')}' />
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
