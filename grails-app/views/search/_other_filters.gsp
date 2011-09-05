<ol class="sub-menu" id="added-options">
	<li class="field" id='contact-name-field'>
		<img src='${resource(dir:'images/icons', file:'contacts.gif')}'" /><h2>Contact Name:</h2>
		<g:textField name="contactSearchString" id="contactSearchString" value="${contactInstance}"/>
	</li>
</ol>
<h2>
	<a id="more-search-options" onclick="expandOptions()"><img src='${resource(dir:'images', file:'move-down.png')}' /></a>
	More search options
</h2>
<ol class="sub-menu" id="expanded-search-options">
	<li><a id='contact-name' onclick="addContactField()">Contact name</a></li>
</ol>