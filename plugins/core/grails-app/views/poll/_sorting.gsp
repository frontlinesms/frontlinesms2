<div id="tabs-3">
	<h2 class="bold">Sort messages automatically using a Keyword (optional)</h2>
	<p class="info">If people send in poll responses using a Keyword, FrontlineSMS can automatically sort the messages on your system.</p>
	<ul class="radios">
		<g:radioGroup name="enableKeyword" values="[false, true]" value="false" labels="['Do not sort messages automatically', 'Sort messages automatically that have the following Keyword:']">
			<li>
				${it.radio}${it.label}
			</li>
		</g:radioGroup>
	</ul>
	<g:textField name="keyword" id="poll-keyword" disabled="disabled"/>
</div>