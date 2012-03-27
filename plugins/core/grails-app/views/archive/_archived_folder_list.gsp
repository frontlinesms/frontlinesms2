<g:if test="${folderInstanceList.size() > 0}">
	<div id="folders">
		<div id="folder-list">
			<table cellspacing=0>
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
								<g:link controller="archive" action="folder" id="ownerId" params="[ownerId: f.id,  viewingMessages: true]">
									${f.name}
								</g:link>
							</td>
							<td class="folder-date-cell">
								<g:link controller="archive" action="folder" id="ownerId" params="[ownerId: f.id,  viewingMessages: true]">
									<g:formatDate date="${f.dateCreated}"/>
								</g:link>
							</td>
							<td class="folder-message-count-cell">
								<g:link controller="archive" action="folder" id="ownerId" params="[ownerId: f.id,  viewingMessages: true]">
									${f.liveMessageCount}
								</g:link>
							</td>
						</tr>
					</g:each>
				</tbody>
			</table>
		</div>
	</div>
</g:if>
<g:else>
	<p>&nbsp No archived folders</p>
</g:else>
