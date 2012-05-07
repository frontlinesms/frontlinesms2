<div id="tabs-3">
	<h2 class="bold"><g:message code="poll.sort.header"/></h2>
	<p class="info"><g:message code="poll.sort.description"/></p>
	<ul class="radios">
		<g:radioGroup name="enableKeyword" values="[false, true]" value="${(activityInstanceToEdit?.keyword?.value as boolean) ?: false}" labels="[g.message(code:'poll.no.automatic.sort'), g.message(code:'poll.sort.automatically')]">
			<li>
				${it.radio}${it.label}
			</li>
		</g:radioGroup>
	</ul>
	<g:if test="${activityInstanceToEdit?.keyword}">
		<g:textField name="keyword" id="poll-keyword" value="${activityInstanceToEdit?.keyword?.value}"/>
	</g:if>
	<g:else>
		<g:textField name="keyword" id="poll-keyword" disabled="true" value="${activityInstanceToEdit?.keyword?.value}"/>
	</g:else>
	
</div>
