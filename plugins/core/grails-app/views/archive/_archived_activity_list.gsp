<g:if test="${activityInstanceTotal > 0}">
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
			<g:each in="${activityInstanceList}" var="a">
				<tr class="activity-list-item">
					<td class="activity-name-cell">
						<g:link controller="archive" action="${a instanceof frontlinesms2.Announcement ? announcement : poll}" params="[messageSection: 'activity', ownerId: a.id, viewingArchive: true, viewingMessages: true]">
							${a.name}
						</g:link>
					</td>
					<td class="activity-type-cell">
						<g:link controller="archive" action="${a instanceof frontlinesms2.Announcement ? announcement : poll}" params="[messageSection: 'activity', ownerId: a.id, viewingArchive: true, viewingMessages: true]">
							${a instanceof frontlinesms2.Announcement ? 'Announcement' : 'Poll'}
						</g:link>
					</td>
					<td class="activity-date-cell">
						<g:link controller="archive" action="${a instanceof frontlinesms2.Announcement ? announcement : poll}" params="[messageSection: 'activity', ownerId: a.id, viewingArchive: true, viewingMessages: true]">
							<g:formatDate date="${a.dateCreated}"/>
						</g:link>
					</td>
					<td class="activity-message-count-cell">
						<g:link controller="archive" action="${a instanceof frontlinesms2.Announcement ? announcement : poll}" params="[messageSection: 'activity', ownerId: a.id, viewingArchive: true, viewingMessages: true]">
							${a.liveMessageCount}
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
