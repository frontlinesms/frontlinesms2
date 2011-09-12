<ol class="sub-menu" id="added-options">
	<li class="field">
		<g:select name="messageStatus" from="${['All sent and received', 'Only received messages', 'Only sent messages']}"
				value="${search?.status}"
				keys="${['', 'INBOUND', 'SENT, SEND_PENDING, SEND_FAILED']}"/>
	</li>
	<li>
		<g:checkBox name="inArchive" value="${search ? (search.inArchive ? true : null) : true}" />Include Archive
	</li>
	<li>
		<h2>${message(code:'default.search.betweendates.title', default:'Between dates:') }</h2>
		<g:datePicker name="startDate" value="${params.startDate ?: new Date()-14}" noSelection="['':'-Choose-']" precision="day"/>
		<br>
		<g:datePicker name="endDate" value="${params.endDate ?: new Date()}" noSelection="['':'-Choose-']" precision="day"/>
	</li>
	<li class="field" id='field-contact-name'>
		<img src='${resource(dir:'images/icons', file:'contacts.gif')}'" /><h2>Contact Name:</h2>
		<g:textField name="contactString" id="contactString" value="${search?.contactString}"/>
		<a onclick="toggleContactNameField()"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
	</li>
	<g:each var="customField" in="${customFieldInstanceList}">
		<li class="field" id="custom-field-${customField.name}">
			<h2>${customField.name}:</h2><br>
			<g:textField name="${customField.name}" id="${customField.name}"/>
			<a onclick="toggleCustomField('${customField.name}')"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
		</li>
	</g:each>
</ol>
<h2>
	<a id="more-search-options" onclick="expandOptions()"><img src='${resource(dir:'images', file:'move-down.png')}' /></a>
	${message(code:'default.search.moresearchoption.label', default:'More search options') }
</h2>
<ol class="sub-menu" id="expanded-search-options">
	<li>
		<a onclick="toggleContactNameField()">Contact Name</a>
	</li>
	<g:each var="customField" in="${customFieldInstanceList}">
		<li>
			<a onclick="toggleCustomField('${customField.name}')">${customField.name}</a>
		</li>
	</g:each>
</ol>