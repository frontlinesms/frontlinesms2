<ul class="sub-list" id="added-options">
	<li class='extra-option field' id='contact-name-list-item'>
		<h3 class="list-title">Contact name:</h3>
		<g:textField name="contactString" value="${search?.contactString}"/>
		<a onclick="toggleExtraSearchOption('contact-name'); removeValue(contact-name);"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
	</li>
	<g:each in="${customFieldList}" status="i" var="c" >
		<li class='extra-option field' id="${c}-list-item">
			<h3 class="list-title">${c}:</h3><br>
			<g:textField name="${c}" value="${(search && search.customFields) ? search?.customFields[c] : ''}"/>
			<a onclick="toggleExtraSearchOption('${c}'); removeValue('${c}');"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
		</li>
	</g:each>
</ul>

<h3 class="list-title">
	<a id="toggle-extra-options">
		<img id="plus" src='${resource(dir:'images/icons', file:'toggle_plus.png')}' />
		<img id="minus" src='${resource(dir:'images/icons', file:'toggle_minus.png')}' />
	</a>
	${message(code:'default.search.moresearchoption.label', default:'More search options') }
</h3>
<ul class="sub-list" id="extra-options-list">
	<li class='field'>
		<a class="extra-option-link" id="contact-name-add" onclick="toggleExtraSearchOption('contact-name')">Contact name</a>
	</li>
	<g:each in="${customFieldList}" status="i" var="f" >
		<li class='field'>
			<a class="extra-option-link" id="${f}-add" onclick="toggleExtraSearchOption('${f}')">${f}</a>
		</li>
	</g:each>
</ul>
