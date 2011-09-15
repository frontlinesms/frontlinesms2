<ol class="sub-menu" id="added-options">
	<li class="field">
		<g:select name="messageStatus" from="${['All sent and received', 'Only received messages', 'Only sent messages']}"
				value="${search?.status}"
				keys="${['', 'INBOUND', 'SENT, SEND_PENDING, SEND_FAILED']}"/>
	</li>
	<li>
		<g:checkBox name="inArchive" value="${search ? (search.inArchive ?: null) : true}" />Include Archive
	</li>
	<li class="field" id='contact-name-field'>
		<img src='${resource(dir:'images/icons', file:'contacts.gif')}'" /><h2>Contact Name:</h2>
		<g:textField name="contactString" id="contactString" value="${search?.contactString}"/>
	</li>
</ol>
<h2>
	<a id="more-search-options" onclick="expandOptions()"><img src='${resource(dir:'images', file:'move-down.png')}' /></a>
	More search options
</h2>
<ol class="sub-menu" id="expanded-search-options">
	<li><a id='contact-name' onclick="addContactField()">Contact name</a></li>
</ol>
