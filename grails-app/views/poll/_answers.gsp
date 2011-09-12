<div id="tabs-2" class="poll-responses-tab">
	<label for='poll-choices'>Enter possible responses (between 2 and 5):</label>
	<ul id='poll-choices'>
		<g:each in="${['A','B','C','D','E']}" var="option">
			<li>
				<label for='choice${option}' class="${option == 'A' || option == 'B' ? 'bold': ''}">${option}</label>
			   	<g:textField class='choices' name="choice${option}" id="choice${option}" value="" />
			</li>
		</g:each>
	</ul>
</div>