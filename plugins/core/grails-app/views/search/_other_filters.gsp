<div id="added-options">
	<div class="input extra-option">
		<label for="contactString"><g:message code="search.contact.name.label"/></label>
		<g:textField name="contactString" value="${search?.contactString}"/>
		<a class="remove-field" onclick="removeValue('contactString'); toggleExtraSearchOption('contactString');"></a>
	</div>
	<g:each in="${customFieldList}" status="i" var="c" >
		<div class="input extra-option" id="${c}-list-item">
			<label for="${c}">${c}:</label>
			<g:textField name="${c}" value="${(search && search.customFields) ? search?.customFields[c] : ''}"/>
			<a class="remove-field" onclick='removeValue("${c}"); toggleExtraSearchOption("${c}");'></a>
		</div>
	</g:each>
</div>

<h3 class="list-title" id="more-options">
	<a id="toggle-extra-options">
		<g:message code="default.search.moreoption.label"/>
	</a>
</h3>
<ul class="sub-list" id="extra-options-list">
	<div class="input">
		<a class="extra-option-link" id="contactString-add" onclick="toggleExtraSearchOption('contactString')">
			<g:message code="search.contact.name"/>
		</a>
	</div>
	<g:each in="${customFieldList}" status="i" var="f" >
		<li class='field'>
			<a class="extra-option-link" id="${f}-add" onclick="toggleExtraSearchOption('${f}')">${f}</a>
		</li>
	</g:each>
</ul>

