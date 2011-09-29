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
		<li class="field">
		<g:select name="messageStatus" from="${['All sent and received', 'Only received messages', 'Only sent messages']}"
				value="${search?.status}"
				keys="${['', 'INBOUND', 'SENT, SEND_PENDING, SEND_FAILED']}"/>
		</li>
		<li>
			<g:checkBox name="inArchive" value="${search ? (search.inArchive ?: null) : true}" />Include Archive
		</li>
	</ol>
</g:if>
<h2>${message(code:'default.search.betweendates.title', default:'Between dates:') }</h2>
<ol class="sub-menu">
	<li class="field">
		<g:datePicker name="startDate" value="${search?.startDate?:'none'}" noSelection="['none':'']" precision="day" years="${2000..1901+(new Date()).year}"/>
		<input type="hidden" class="datepicker"/>
     </li>
     <li class="field">
     	<g:datePicker name="endDate" value="${search?.endDate}" noSelection="['':'']" precision="day" years="${2000..1901+(new Date()).year}"/>
		<input type="hidden" class="datepicker">
	</li>
</ol>