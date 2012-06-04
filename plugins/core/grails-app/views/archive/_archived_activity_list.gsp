<table id="list" class="archive">
	<thead>
		<tr>
			<th class="name"><g:message code="archive.activity.name"/></th>
			<th><g:message code="archive.activity.type"/></th>
			<th><g:message code="archive.activity.date"/></th>
			<th><g:message code="archive.activity.messages"/></th>
		</tr>
	</thead>
	<tbody>
		<g:if test="${activityInstanceTotal > 0}">
			<g:each in="${activityInstanceList}" var="a">
				<tr>
					<td>
						<g:link controller="archive" action="${a instanceof frontlinesms2.Announcement ? announcement : poll}" params="[messageSection: 'activity', ownerId: a.id, viewingMessages: true]">
							${a.name}
						</g:link>
					</td>
					<td>
						<g:link controller="archive" action="${a instanceof frontlinesms2.Announcement ? announcement : poll}" params="[messageSection: 'activity', ownerId: a.id, viewingMessages: true]">
							${a instanceof frontlinesms2.Announcement ? 'Announcement' : 'Poll'}
						</g:link>
					</td>
					<td>
						<g:link controller="archive" action="${a instanceof frontlinesms2.Announcement ? announcement : poll}" params="[messageSection: 'activity', ownerId: a.id, viewingMessages: true]">
							<fsms:unbroken>
								<g:formatDate date="${a.dateCreated}"/>
							</fsms:unbroken>
						</g:link>
					</td>
					<td>
						<g:link controller="archive" action="${a instanceof frontlinesms2.Announcement ? announcement : poll}" params="[messageSection: 'activity', ownerId: a.id, viewingMessages: true]">
							${a.liveMessageCount}
						</g:link>
					</td>
				</tr>
			</g:each>
		</g:if>
		<g:else>
			<tr>
				<td colspan="4" class="no-content">
					<g:message code="archive.activity.list.none"/>
				</td>
			</tr>
		</g:else>
	</tbody>
</table>

