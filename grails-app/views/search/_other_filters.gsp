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
		<g:textField name="contactString" value="${search?.contactString}"/>
		<a onclick="toggleContactNameField()"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
	</li>
	<g:if test="${search==null}">
		<g:each var="customField" in="${customFieldList}">
			<li class="field" id="custom-field-field-${customField}">
				<h2>${customField}:</h2><br>
				<g:textField name="${customField}CustomField" />
				<a onclick="toggleCustomField('${customField}')"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
			</li>
		</g:each>
	</g:if>
	<g:else>
		<g:each var="customField" in="${search.usedCustomField}">
			<li class="field" id="custom-field-field-${customField.key}">
				<h2>${customField.key}:</h2><br>
				<g:textField name="${customField.key}CustomField" value="${customField.value}"/>
				<a onclick="toggleCustomField('${customField.key}')"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
			</li>
		</g:each>
	</g:else>
</ol>
<h2>
	<a id="more-search-options"><img src='${resource(dir:'images', file:'move-down.png')}' /></a>
	${message(code:'default.search.moresearchoption.label', default:'More search options') }
</h2>
<ol class="sub-menu" id="expanded-search-options">
	<li id="field-link-contact-name">
		<a onclick="toggleContactNameField()">Contact Name</a>
	</li>
	<g:each var="customField" in="${customFieldList}">
		<li id="custom-field-link-${customField}">
			<a onclick="toggleCustomField('${customField}')">${customField}</a>
		</li>
	</g:each>
</ol>