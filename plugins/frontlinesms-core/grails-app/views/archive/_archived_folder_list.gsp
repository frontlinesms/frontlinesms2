<table id="main-list" class="archive">
	<thead>
		<tr>
			<th class="name"><g:message code="archive.folder.name"/></th>
			<th><g:message code="archive.folder.date"/></</th>
			<th><g:message code="archive.folder.messages"/></th>
		</tr>
	</thead>
	<tbody>
		<g:if test="${folderInstanceList.size() > 0}">
			<g:each in="${folderInstanceList}" var="f">
				<tr class="folder-list-item">
					<td class="folder-name-cell">
						<g:link controller="archive" action="folder" params="[ownerId: f.id,  viewingMessages: true, messageSection: 'folder']">
							${f.name}
						</g:link>
					</td>
					<td class="folder-date-cell">
						<g:link controller="archive" action="folder" params="[ownerId: f.id,  viewingMessages: true, messageSection: 'folder']">
							<fsms:unbroken>
								<g:formatDate date="${f.dateCreated}"/>
							</fsms:unbroken>
						</g:link>
					</td>
					<td class="folder-message-count-cell">
						<g:link controller="archive" action="folder" params="[ownerId: f.id,  viewingMessages: true, messageSection: 'folder']">
							${f.liveMessageCount}
						</g:link>
					</td>
				</tr>
			</g:each>
		</g:if>
		<g:else>
			<tr>
				<td colspan="3" class="no-content">
					<g:message code="archive.folder.none"/>
				</td>
			</tr>
		</g:else>
	</tbody>
</table>
