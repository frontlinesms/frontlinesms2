<g:if test="${folderInstanceList.size() > 0}">
	<div id="folders" class="${(messageSection == 'inbox' || messageSection == 'sent' || messageSection == 'pending' || messageSection == 'trash' || messageSection == 'radioShow') ? '' : 'tall-header'}">
		<table id="folder-list" cellspacing=0>
			<thead>
				<tr>
					<th class="folder-name-cell">Name</th>
					<th class="folder-date-cell">Date</th>
					<th class="folder-message-count-cell">Messages</th>
				</tr>
			</thead>
			<tbody>
				<g:each in="${folderInstanceList}" var="f">
					<tr class="folder-list-item">
						<td class="folder-name-cell">
							<g:link controller="archive" action="folder" id="ownerId" params="[ownerId: f.id, viewingArchive: true, viewingMessages: true]">
								${f.name}
							</g:link>
						</td>
						<td class="folder-date-cell">
							<g:link controller="archive" action="folder" id="ownerId" params="[ownerId: f.id, viewingArchive: true, viewingMessages: true]">
								<g:formatDate date="${f.dateCreated}"/>
							</g:link>
						</td>
						<td class="folder-message-count-cell">
							<g:link controller="archive" action="folder" id="ownerId" params="[ownerId: f.id, viewingArchive: true, viewingMessages: true]">
								${f.liveMessageCount}
							</g:link>
						</td>
					</tr>
				</g:each>
			</tbody>
		</table>
	</div>
</g:if>
<g:else>
	No archived folders
</g:else>
