<ol class="sub-menu" id="added-options">
	
	<li class="field" id='more-option-field-contact-name'>
		<img src='${resource(dir:'images/icons', file:'contacts.png')}'" /><h2>Contact name:</h2>
		<g:textField name="contactString" value="${search?.contactString}"/>
		<a onclick="toggleMoreOptionElement('contact-name')"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
	</li>
	
	<g:if test="${search == null}">
		<g:each var="customField" in="${customFieldList}">
			<li class="field" id="more-option-field-custom-field-${customField}">
				<h2>${customField}:</h2><br>
				<g:textField name="${customField}CustomField" />
				<a onclick="toggleMoreOptionElement('custom-field-${customField}')"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
			</li>
		</g:each>
	</g:if>
	<g:else>
		<g:each var="customField" in="${search.customFields}">
			<li class="field" id="more-option-field-custom-field-${customField.key}">
				<h2>${customField.key}:</h2><br>
				<g:textField name="${customField.key}CustomField" value="${customField.value}"/>
				<a onclick="toggleMoreOptionElement('custom-field-${customField.key}')"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
			</li>
		</g:each>
	</g:else>
</ol>

<h2>
	<a id="more-search-options"><img src='${resource(dir:'images', file:'move-down.png')}' /></a>
	${message(code:'default.search.moresearchoption.label', default:'More search options') }
</h2>
<ol class="sub-menu" id="expanded-search-options">
	<li>
		<a id="more-option-link-contact-name" onclick="toggleMoreOptionElement('contact-name')">Contact name</a>
	</li>
	<g:each var="customField" in="${customFieldList}">
		<li>
			<a id="more-option-link-custom-field-${customField}" onclick="toggleMoreOptionElement('custom-field-${customField}')">${customField}</a>
		</li>
	</g:each>
</ol>
