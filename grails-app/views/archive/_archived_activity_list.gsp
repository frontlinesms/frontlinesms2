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
					<g:link controller="archive" action="poll" params="[ownerId: p.id, viewingArchive: true, viewingMessages: true]">
						${p.title}
					</g:link>
				</td>
				<td>
					<g:link controller="archive" action="poll" params="[ownerId: p.id, viewingArchive: true, viewingMessages: true]">
						Poll
					</g:link>
				</td>
				<td>
					<g:link controller="archive" action="poll" params="[ownerId: p.id, viewingArchive: true, viewingMessages: true]">
						<g:formatDate date="${p.dateCreated}"/>
					</g:link>
				</td>
				<td>
					<g:link controller="archive" action="poll" params="[ownerId: p.id, viewingArchive: true, viewingMessages: true]">
						${p.liveMessageCount}
					</g:link>
				</td>
			</tr>
		</g:each>
		<g:each in="${announcementInstanceList}" var="a">
			<tr>
				<td>
					<g:link controller="archive" action="announcement" params="[ownerId: a.id, viewingArchive: true, viewingMessages: true]">
						${a.name}
					</g:link>
				</td>
				<td>
					<g:link controller="archive" action="announcement" params="[ownerId: a.id, viewingArchive: true, viewingMessages: true]">
						Announcement
					</g:link>
				</td>
				<td>
					<g:link controller="archive" action="announcement" params="[ownerId: a.id, viewingArchive: true, viewingMessages: true]">
						<g:formatDate date="${a.dateCreated}"/>
					</g:link>
				</td>
				<td>
					<g:link controller="archive" action="announcement" params="[ownerId: a.id, viewingArchive: true, viewingMessages: true]">
						${a.liveMessageCount}
					</g:link>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
</g:if>
<g:else>
	No archived activities
</g:else>
