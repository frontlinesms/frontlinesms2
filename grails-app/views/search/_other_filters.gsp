<ul class="sub-list" id="added-options">
	<li class='extra-option' id='contactString-list-item'>
		<h3 class="list-title">Contact name:</h3>
		<g:textField name="contactString" value="${search?.contactString}"/>
		<a onclick="removeValue('contactString'); toggleExtraSearchOption('contactString');"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
	</li>
	<g:each in="${customFieldList}" status="i" var="c" >
		<li class='extra-option' id="${c}-list-item">
			<h3 class="list-title">${c}:</h3>
			<g:textField name="${c}" value="${(search && search.customFields) ? search?.customFields[c] : ''}"/>
			<a onclick='removeValue("${c}"); toggleExtraSearchOption("${c}");'><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
		</li>
	</g:each>
</ul>

<h3 class="list-title" id="more-options">
	<a id="toggle-extra-options">
		<img id="plus" src='${resource(dir:'images/icons', file:'toggle_plus.png')}' />
		<img id="minus" src='${resource(dir:'images/icons', file:'toggle_minus.png')}' />
	</a>
	${message(code:'default.search.moreoption.label', default:'More options') }
</h3>
<ul class="sub-list" id="extra-options-list">
	<li class='field'>
		<a class="extra-option-link" id="contactString-add" onclick="toggleExtraSearchOption('contactString')">Contact name</a>
	</li>
	<g:each in="${customFieldList}" status="i" var="f" >
		<li class='field'>
			<a class="extra-option-link" id="${f}-add" onclick="toggleExtraSearchOption('${f}')">${f}</a>
		</li>
	</g:each>
</ul>
