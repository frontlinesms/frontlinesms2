<div id="tabs-3">
	<h2 class="bold">Sort messages automatically using a Keyword (optional)</h2>
	<p class="info">If people send in poll responses using a Keyword, FrontlineSMS can automatically sort the messages on your system.</p>
	<ul class="radios">
		<div>${activityInstanceToEdit?.keyword as boolean}</div>
		<g:radioGroup name="enableKeyword" values="[false, true]" value="${(activityInstanceToEdit?.keyword as boolean) ?: false}" labels="['Do not sort messages automatically', 'Sort messages automatically that have the following Keyword:']">
			<li>
				${it.radio}${it.label}
			</li>
		</g:radioGroup>
	</ul>
	<g:if test="${activityInstanceToEdit?.keyword}">
		<g:textField name="keyword" id="poll-keyword" value="${activityInstanceToEdit?.keyword}"/>
	</g:if>
	<g:else>
		<g:textField name="keyword" id="poll-keyword" disabled="true" value="${activityInstanceToEdit?.keyword}"/>
	</g:else>
	
</div>
