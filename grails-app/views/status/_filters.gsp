<g:if test="${groupInstanceList || pollInstanceList}">
	<ol class="sub-menu">
		<li class='field'>
			<g:select name="groupId" from="${groupInstanceList}" value="${search?.group?.id}"
					  optionKey="id" optionValue="name"
					  noSelection="${['':'Show all groups']}"/>
		</li>
		<li class="field">
			<g:select name="activityId" from="${pollInstanceList + folderInstanceList}"
					  value="${search?.activityId}"
					  optionKey="${{(it instanceof frontlinesms2.Poll?'poll':'folder') + '-' + it.id}}"
					  optionValue="${{it instanceof frontlinesms2.Poll? it.title: it.name}}"
					  noSelection="${['':'Show all activities/folders']}"/>
		</li>
	</ol>
</g:if>
<div>
	<g:radio name="rangeOption" value="two-weeks" checked="${params.rangeOption == 'two-weeks'}" />
	<g:select id="pre-defined-range-options" name='pre-defined-range-options'
	    noSelection="${['null':'Show last two weeks']}" >
	</g:select>
</div>
<div>
	<g:radio name="rangeOption" value="between-dates" checked="${params.rangeOption == 'between-dates'}"/>Between dates
</div>
<g:datePicker name="startDate" value="${params['startDate'] ?: new Date()-14}" noSelection="['':'-Choose-']" precision="day"/>
<g:datePicker name="endDate" value="${params['endDate'] ?: new Date()}" noSelection="['':'-Choose-']" precision="day"/>