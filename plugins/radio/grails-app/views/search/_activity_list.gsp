<li class='field'>
	<g:select class="dropdown" name="activityId" from="${pollInstanceList + folderInstanceList + radioShowInstanceList}"
			  value="${search?.activityId}"
			  optionKey="${{(it instanceof frontlinesms2.Poll?'poll':'folder') + '-' + it.id}}"
			  optionValue="${{it instanceof frontlinesms2.Poll? it.title: it.name}}"
			  noSelection="${['':'Select activity/folder/RadioShow']}"/>
</li>