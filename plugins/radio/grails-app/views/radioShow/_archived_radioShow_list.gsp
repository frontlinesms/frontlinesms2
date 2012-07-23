<table id="main-list" class="archive">
	<thead>
		<tr>
			<th class="name"><g:message code="archive.radioshow.name"/></th>
			<th><g:message code="archive.radioshow.type"/></</th>
			<th><g:message code="archive.radioshow.messages"/></th>
		</tr>
	</thead>
	<tbody>
		<g:if test="${showInstanceList.size() > 0}">
			<g:each in="${showInstanceList}" var="s">
				<tbody>
					<tr class="folder-list-item">
						<td class="show-name-cell show-cell">
							<g:link controller="radioShow" action="radioShow" params="[ownerId: s.id,  viewingMessages: true, messageSection: 'radioShow', inArchive:true]">
								${s.name}
							</g:link>
						</td>
						<td class="show-type-cell show-cell">
							<g:link controller="archive" action="folder" params="[ownerId: s.id,  viewingMessages: true, messageSection: 'folder']">
								<fsms:unbroken>
									<g:message code="radio.label"/>
								</fsms:unbroken>
							</g:link>
						</td>
						<td class="show-message-count-cell show-cell">
							<g:link controller="archive" action="folder" params="[ownerId: s.id,  viewingMessages: true, messageSection: 'folder']">
								${s.liveMessageCount}
							</g:link>
						</td>
					</tr>
					<g:each in="${s.activities}" var="a">
						<tr>
							<td class="show-activity-name">
								<g:link controller="archive" action="${a.shortName}" params="[messageSection:'radioShow', ownerId:a.id, viewingMessages:true]">
									- ${a.name}
								</g:link>
							</td>
							<td>
								<g:link controller="archive" action="${a.shortName}" params="[messageSection:'radioShow', ownerId:a.id, viewingMessages:true]">
									<g:message code="${a.shortName}.label"/>
								</g:link>
							</td>
							<td>
								<g:link controller="archive" action="${a.shortName}" params="[messageSection:'radioShow', ownerId:a.id, viewingMessages:true]">
									${a.liveMessageCount}
								</g:link>
							</td>
						</tr>
					</g:each>
				</tbody>
			</g:each>
		</g:if>
		<g:else>
			<tr>
				<td colspan="3" class="no-content">
					<g:message code="archive.radioshow.none"/>
				</td>
			</tr>
		</g:else>
	</tbody>
</table>
