<div id="tabs-2" class="poll-responses-tab">
	<label for='instruction'>Enter Instructions:</label>
	<g:textField name="instruction" id="instruction" value="" />
	<label for='poll-choices'>Enter possible responses (between 2 and 5):</label>
	<ul id='poll-choices'>
		<g:each in="${['A','B','C','D','E']}" var="option">
			<li>
				<label for='choice${option}'>${option}</label>
			   	<g:textField class='choices' name="choice${option}" id="choice${option}" value="" />
			</li>
		</g:each>
	</ul>
</div>