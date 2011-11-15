<ul class="sub-list" id="added-options">
	
	<li class='field' id='more-option-field-contact-name'>
		<img src='${resource(dir:'images/icons', file:'contacts.png')}'" /><h3 class="list-title">Contact name:</h3>
		<g:textField name="contactString" value="${search?.contactString}"/>
		<a onclick="toggleMoreOptionElement('contact-name')"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
	</li>
	<g:each var="customField" in="${search?.customFields}">
		<li class='field' id="more-option-field-custom-field-${customField.key}">
			<h3 class="list-title">${customField?.key}:</h3><br>
			<g:textField name="${customField?.key}CustomField" value="${customField.value}"/>
			<a onclick="toggleMoreOptionElement('custom-field-${customField.key}')"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
		</li>
	</g:each>
</ul>

<h3 class="list-title">
	<a id="more-search-options"><img src='${resource(dir:'images', file:'move-down.png')}' /></a>
	${message(code:'default.search.moresearchoption.label', default:'More search options') }
</h3>
<ul class="sub-list" id="expanded-search-options">
	<li>
		<a id="more-option-link-contact-name" onclick="toggleMoreOptionElement('contact-name')">Contact name</a>
	</li>
	<g:each var="customField" in="${customFieldList}">
		<li>
			<a id="more-option-link-custom-field-${customField}" onclick="toggleMoreOptionElement('custom-field-${customField}')">${customField}</a>
		</li>
	</g:each>
</ul>
