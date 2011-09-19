<g:if test="${groupInstanceList || pollInstanceList}">
	<h2>Limit Search to:</h2>
	<ol class="sub-menu">
		<li class='field'>
			<g:select name="groupId" from="${groupInstanceList}" value="${search?.group?.id}"
					  optionKey="id" optionValue="name"
					  noSelection="${['':'Select group']}"/>
		</li>
		<li class="field">
			<g:select name="activityId" from="${pollInstanceList + folderInstanceList}"
					  value="${search?.activityId}"
					  optionKey="${{(it instanceof frontlinesms2.Poll?'poll':'folder') + '-' + it.id}}"
					  optionValue="${{it instanceof frontlinesms2.Poll? it.title: it.name}}"
					  noSelection="${['':'Select activity / folder']}"/>
		</li>
	</ol>
</g:if>