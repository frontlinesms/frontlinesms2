<g:if test="${groupInstanceList || activityInstanceList || folderInstanceList}">
	<ul id="type-filters" class="sub-menu">
		<li>
			<g:select class="dropdown" name="groupId" onChange="submit()" from="${groupInstanceList}" value="${search?.group?.id}"
					  optionKey="id" optionValue="name"
					  noSelection="${['':g.message(code:'traffic.allgroups')]}"/>
		</li>
				
		<li>
			<g:select class="dropdown" name="activityId" onChange="submit()" from="${activityInstanceList + folderInstanceList}"
					  value="${search?.activityId}"
					  optionKey="${{it.id}}"
					  optionValue="${g.message(code:"${it.shortName}.title", args:[it.name])}"
					  noSelection="${['':g.message(code:'traffic.all.folders.activities')]}"/>
		</li>
	</ul>
</g:if>
<ul id="time-filters">
	<li>
		<g:radio name="rangeOption" value="two-weeks" checked="${params.rangeOption == 'two-weeks'}"/>
		<span id="dates-text"><g:message code="traffic.filter.2weeks"/></span>
	</li>
	<li>
		<g:radio name="rangeOption" value="between-dates" checked="${params.rangeOption == 'between-dates'}"/>
		<span id="dates-text"><g:message code="traffic.filter.between.dates"/></span>
	</li>
	<li class='datepicker'><g:datePicker class='datepicker' name="startDate" value="${params['startDate'] ?: new Date()-14}" noSelection="['':'-Choose-']" precision="day"/></li>
	<li class='datepicker'><g:datePicker class='datepicker' name="endDate" value="${params['endDate'] ?: new Date()}" noSelection="['':'-Choose-']" precision="day"/></li>
</ul>
