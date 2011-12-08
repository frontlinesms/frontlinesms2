<g:if test="${groupInstanceList || pollInstanceList}">
	<ul id="type-filters" class="sub-menu">
		<li>
			<g:select class="dropdown" name="groupId" from="${groupInstanceList}" value="${search?.group?.id}"
					  optionKey="id" optionValue="name"
					  noSelection="${['':'Show all groups']}"/>
		</li>
		<li>
			<g:select class="dropdown" name="activityId" from="${pollInstanceList + folderInstanceList}"
					  value="${search?.activityId}"
					  optionKey="${{(it instanceof frontlinesms2.Poll?'poll':'folder') + '-' + it.id}}"
					  optionValue="${{it instanceof frontlinesms2.Poll? it.title: it.name}}"
					  noSelection="${['':'Show all activities/folders']}"/>
		</li>
	</ul>
</g:if>
<ul id="time-filters">
	<li>
		<g:radio name="rangeOption" value="two-weeks" checked="${params.rangeOption == 'two-weeks'}" />
		<g:select class="dropdown" id="pre-defined-range-options" name='pre-defined-range-options'
		    noSelection="${['null':'Show last two weeks']}" >
		</g:select>
	</li>
	<li>
		<g:radio name="rangeOption" value="between-dates" checked="${params.rangeOption == 'between-dates'}"/>Between dates
	</li>
	<li class='datepicker'><g:datePicker class='datepicker' name="startDate" value="${params['startDate'] ?: new Date()-14}" noSelection="['':'-Choose-']" precision="day"/></li>
	<li class='datepicker'><g:datePicker class='datepicker' name="endDate" value="${params['endDate'] ?: new Date()}" noSelection="['':'-Choose-']" precision="day"/></li>
</ul>