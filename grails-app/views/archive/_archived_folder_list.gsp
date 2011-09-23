<g:if test="${folderInstanceList.size() > 0}">
<table id="folder-table">
	<thead>
	<tr>
		<th>Name</th>
		<th>Date</th>
		<th>Messages</th>
	</tr>
	</thead>
	<tbody>
		<g:each in="${folderInstanceList}" var="f">
			<tr>
				<td class="folder-name">
					<g:link controller="archive" action="folder" id="ownerId" params="[ownerId: f.id, viewingMessages: true]">
						${f.name}
					</g:link>
				</td>
				<td>
					<g:link controller="archive" action="folder" id="ownerId" params="[ownerId: f.id, viewingMessages: true]">
						<g:formatDate date="${f.dateCreated}"/>
					</g:link>
				</td>
				<td>
					<g:link controller="archive" action="folder" id="ownerId" params="[ownerId: f.id, viewingMessages: true]">
						${f.liveMessageCount}
					</g:link>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
</g:if>
<g:else>
	No archived folders
</g:else>
