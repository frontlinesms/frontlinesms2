<g:if test="${groupInstanceList || pollInstanceList}">
	<h3 class="list-title"><g:message code="search.filter.label" /></h3>
	<ul class="sub-list" id="non-dates">
		<li class='field'>
			<g:select class="dropdown" name="groupId" from="${groupInstanceList}" value="${search?.group?.id}"
					  optionKey="id" optionValue="name"
					  noSelection="${['': g.message(code:'search.filder.group')]}"/>
		</li>
		<g:render template="../search/activity_list" plugin="${grailsApplication.config.frontlinesms2.plugin}"/>
		<li class='field'>
		<g:select class="dropdown" name="messageStatus" from="${[g.message(code:'search.filter.messages.all'), g.message(code:'search.filter.messages.inbox'), g.message(code:'search.filter.messages.sent')]}"
				value="${search?.status}"
				keys="${['', 'inbound', 'outbound']}"/>
		</li>
		<li class='field'>
			<g:checkBox name="inArchive" value="${search ? (search.inArchive ?: null) : true}" /><g:message code="search.filter.archive" />
		</li>
	</ul>
</g:if>
<h3 class="list-title">${message(code:'default.search.betweendates.title', default: g.message(code:'search.betweendates.label')) }</h3>
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
