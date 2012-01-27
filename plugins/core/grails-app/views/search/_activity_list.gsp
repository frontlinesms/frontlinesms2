<li class='field'>
	<g:select class="dropdown" name="activityId" from="${pollInstanceList + folderInstanceList + announcementInstanceList}"
			  value="${search?.activityId}"
			  optionKey="${{(it instanceof frontlinesms2.Activity ? 'activity' : 'folder') + '-' + it.id}}"
			  optionValue="${{it.name}}"
			  noSelection="${['':'Select activity / folder']}"/>
</li>
