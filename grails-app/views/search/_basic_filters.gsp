<g:if test="${groupInstanceList || pollInstanceList}">
	<h3 class="list-title">Limit Search to:</h3>
	<ul class="sub-list" id="non-dates">
		<li class='field'>
			<g:select class="dropdown" name="groupId" from="${groupInstanceList}" value="${search?.group?.id}"
					  optionKey="id" optionValue="name"
					  noSelection="${['':'Select group']}"/>
		</li>
		<li class='field'>
			<g:select class="dropdown" name="activityId" from="${pollInstanceList + folderInstanceList}"
					  value="${search?.activityId}"
					  optionKey="${{(it instanceof frontlinesms2.Poll?'poll':'folder') + '-' + it.id}}"
					  optionValue="${{it instanceof frontlinesms2.Poll? it.title: it.name}}"
					  noSelection="${['':'Select activity / folder']}"/>
		</li>
		<li class='field'>
		<g:select class="dropdown" name="messageStatus" from="${['All sent and received', 'Only received messages', 'Only sent messages']}"
				value="${search?.status}"
				keys="${['', 'INBOUND', 'SENT, SEND_PENDING, SEND_FAILED']}"/>
		</li>
		<li class='field'>
			<g:checkBox name="inArchive" value="${search ? (search.inArchive ?: null) : true}" />Include Archive
		</li>
	</ul>
</g:if>
<h3 class="list-title">${message(code:'default.search.betweendates.title', default:'Between dates:') }</h3>
<ul class="sub-list">
	<li class='field'>
		<g:datePicker class='datepicker' name="startDate" value="${search?.startDate ?: 'none'}" noSelection="['none':'']" precision="day" years="${2000..1901+(new Date()).year}"/>
		<input type="hidden" class="datepicker"/>
     </li>
     <li class='field'>
     	<g:datePicker class='datepicker' name="endDate" value="${search?.endDate ?: 'none'}" noSelection="['none':'']" precision="day" years="${2000..1901+(new Date()).year}"/>
		<input type="hidden" class="datepicker">
	</li>
</ul>