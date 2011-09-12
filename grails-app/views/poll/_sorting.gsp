<div id="tabs-3">
	<h3>Sort messages automatically using a Keyword (optional)</h3>
	<p class="info">If people send in poll responses using a Keyword, FrontlineSMS can automatically sort the messages on your system.</p>
	<g:radioGroup name="enableKeyword" values="[false, true]" value="false" labels="['Do not sort messages automatically', 'Sort messages automatically that have the following Keyword:']">
		<div>
			${it.radio}
			<label for="TODO">${it.label}</label>
		</div>
	</g:radioGroup>
	<g:textField name="keyword" id="poll-keyword" disabled="disabled"/>
</div>