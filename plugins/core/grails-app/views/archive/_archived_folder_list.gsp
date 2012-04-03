<g:if test="${folderInstanceList.size() > 0}">
	<div id="folders">
		<div id="folder-list" class="message-list">
			<table cellspacing=0>
				<thead>
					<tr>
						<th class="folder-name-cell"><g:message code="archive.folder.name" /></</th>
						<th class="folder-date-cell"><g:message code="archive.folder.date" /></</th>
						<th class="folder-message-count-cell"><g:message code="archive.folder.messages" /></th>
					</tr>
				</thead>
				<tbody>
					<g:each in="${folderInstanceList}" var="f">
						<tr class="folder-list-item">
							<td class="folder-name-cell">
								<g:link controller="archive" action="folder" params="[ownerId: f.id,  viewingMessages: true, messageSection: 'folder']">
									${f.name}
								</g:link>
							</td>
							<td class="folder-date-cell">
								<g:link controller="archive" action="folder" params="[ownerId: f.id,  viewingMessages: true, messageSection: 'folder']">
									<g:formatDate date="${f.dateCreated}"/>
								</g:link>
							</td>
							<td class="folder-message-count-cell">
								<g:link controller="archive" action="folder" params="[ownerId: f.id,  viewingMessages: true, messageSection: 'folder']">
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
	<p><g:message code="archive.folder.none" /></p>
</g:else>
