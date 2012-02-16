<g:if test="${groupInstanceList || pollInstanceList}">
	<h3 class="list-title">Limit Search to:</h3>
	<ul class="sub-list" id="non-dates">
		<li class='field'>
			<g:select class="dropdown" name="groupId" from="${groupInstanceList}" value="${search?.group?.id}"
					  optionKey="id" optionValue="name"
					  noSelection="${['':'Select group']}"/>
		</li>
		<g:render template="../search/activity_list" plugin="${grailsApplication.config.frontlinesms2.plugin}"/>
		<li class='field'>
		<g:select class="dropdown" name="messageStatus" from="${['All sent and received', 'Only received messages', 'Only sent messages']}"
				value="${search?.status}"
				keys="${['', 'inbound', 'outbound']}"/>
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
