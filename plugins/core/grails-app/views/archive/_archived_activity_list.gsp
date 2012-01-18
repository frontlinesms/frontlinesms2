<g:if test="${itemInstanceTotal > 0}">
<div id="activities">
	<table id="activity-list">
		<thead>
			<tr>
				<th class="activity-name-cell">Name</th>
				<th class="activity-type-cell">Type</th>
				<th class="activity-date-cell">Date</th>
				<th class="activity-message-count-cell">Messages</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${announcementInstanceList}" var="a">
				<tr class="activity-list-item">
					<td class="activity-name-cell">
						<g:link controller="archive" action="announcement" params="[messageSection: 'announcement', ownerId: a.id, viewingArchive: true, viewingMessages: true]">
							${a.name}
						</g:link>
					</td>
					<td class="activity-type-cell">
						<g:link controller="archive" action="announcement" params="[messageSection: 'announcement', ownerId: a.id, viewingArchive: true, viewingMessages: true]">
							Announcement
						</g:link>
					</td>
					<td class="activity-date-cell">
						<g:link controller="archive" action="announcement" params="[messageSection: 'announcement', ownerId: a.id, viewingArchive: true, viewingMessages: true]">
							<g:formatDate date="${a.dateCreated}"/>
						</g:link>
					</td>
					<td class="activity-message-count-cell">
						<g:link controller="archive" action="announcement" params="[messageSection: 'announcement', ownerId: a.id, viewingArchive: true, viewingMessages: true]">
							${a.liveMessageCount}
						</g:link>
					</td>
				</tr>
			</g:each>
			<g:each in="${pollInstanceList}" var="p">
				<tr class="activity-list-item">
					<td class="activity-name-cell">
						<g:link controller="archive" action="poll" params="[messageSection: 'poll', ownerId: p.id, viewingArchive: true, viewingMessages: true]">
							${p.title}
						</g:link>
					</td>
					<td class="activity-type-cell">
						<g:link controller="archive" action="poll" params="[messageSection: 'poll', ownerId: p.id, viewingArchive: true, viewingMessages: true]">
							Poll
						</g:link>
					</td>
					<td  class="activity-date-cell">
						<g:link controller="archive" action="poll" params="[messageSection: 'poll', ownerId: p.id, viewingArchive: true, viewingMessages: true]">
							<g:formatDate date="${p.dateCreated}"/>
						</g:link>
					</td>
					<td class="activity-message-count-cell">
						<g:link controller="archive" action="poll" params="[messageSection: 'poll', ownerId: p.id, viewingArchive: true, viewingMessages: true]">
							${p.liveMessageCount}
						</g:link>
					</td>
				</tr>
			</g:each>
		</tbody>
	</table>
</div>
</g:if>
<g:else>
	No archived activities
</g:else>
