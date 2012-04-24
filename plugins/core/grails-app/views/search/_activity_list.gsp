<li class='field'>
	<g:select class="dropdown" name="activityId" from="${activityInstanceList + folderInstanceList}"
			  value="${search?.activityId}"
			  optionKey="${{(it instanceof frontlinesms2.Activity ? 'activity' : 'folder') + '-' + it.id}}"
			  optionValue="${{it.name}}"
			  noSelection="${['':g.message(code:'search.filter.activities')]}"/>
</li>
