	<div id="search-filters">
		<g:if test="${groupInstanceList || pollInstanceList}">
			<label>Limit Search to:</label>
			<div class="field">
				<g:select name="groupId" from="${groupInstanceList}" value="${groupInstance?.id}"
						  optionKey="id" optionValue="name"
						  noSelection="${['':'Select group']}"/>
			</div>
			<div class="field">
				<g:select name="activityId" from="${pollInstanceList + folderInstanceList}"
						  value="${activityId}"
						  optionKey="${{(it instanceof frontlinesms2.Poll?'poll':'folder') + '-' + it.id}}"
						  optionValue="${{it instanceof frontlinesms2.Poll? it.title: it.name}}"
						  noSelection="${['':'Select activity / folder']}"/>
			</div>
		</g:if>
	</div>