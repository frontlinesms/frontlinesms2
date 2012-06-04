<g:if test="${groupInstanceList || pollInstanceList}">
	<h3 class="list-title"><g:message code="search.filter.label"/></h3>
	<ul class="sub-list" id="non-dates">
		<li class='field'>
			<g:select class="dropdown" name="groupId" from="${groupInstanceList}" value="${search?.group?.id}"
					  optionKey="id" optionValue="name"
					  noSelection="${['': g.message(code:'search.filter.group')]}"/>
		</li>
		<fsms:render template="/search/activity_list"/>
		<li class='field'>
		<g:select class="dropdown" name="messageStatus" from="${[g.message(code:'search.filter.messages.all'), g.message(code:'search.filter.inbox'), g.message(code:'search.filter.sent')]}"
				value="${search?.status}"
				keys="${['', 'inbound', 'outbound']}"/>
		</li>
		<li class='field'>
			<g:checkBox name="inArchive" value="${search ? (search.inArchive ?: null) : true}"/><g:message code="search.filter.archive"/>
		</li>
	</ul>
</g:if>
<h3 class="list-title">${message(code:'default.search.betweendates.title', default: 'Between dates:')}</h3>
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
