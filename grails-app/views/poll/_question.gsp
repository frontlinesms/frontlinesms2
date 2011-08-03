<div id="tabs-1">
	<div class="section">
		<div>
			<h3>Select the kind of poll to create</h3>
			<g:radio name="poll-type" value="standard" onclick="populateResponses()"/>Question with a 'Yes' or 'No' answer
			<g:radio name="poll-type" value="multiple"  onclick="populateResponses()"/>Multiple choice question (e.g. 'Red', 'Blue', 'Green')
		</div>
	</div>
	<g:link url="#" onclick="moveForward()">Next</g:link>
</div>