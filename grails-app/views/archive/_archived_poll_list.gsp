<g:if test="${pollInstanceList.size() > 0}">
<table id="list-items">
	<thead>
	<tr>
		<th>Name</th>
		<th>Type</th>
		<th>Date</th>
		<th>Messages</th>
	</tr>
	</thead>
	<tbody>
		<g:each in="${pollInstanceList}" var="p">
			<tr>
				<td>
					<g:link controller="archive" action="poll" params="[ownerId: p.id, viewingMessages: true]">
						${p.title}
					</g:link>
				</td>
				<td>
					<g:link controller="archive" action="poll" params="[ownerId: p.id, viewingMessages: true]">
						Poll
					</g:link>
				</td>
				<td>
					<g:link controller="archive" action="poll" params="[ownerId: p.id, viewingMessages: true]">
						<g:formatDate date="${p.dateCreated}"/>
					</g:link>
				</td>
				<td>
					<g:link controller="archive" action="poll" params="[ownerId: p.id, viewingMessages: true]">
						${p}
					</g:link>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
</g:if>
<g:else>
	No archived polls
</g:else>
