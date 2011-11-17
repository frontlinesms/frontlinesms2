<ul class="sub-list" id="added-options">
	<li class='field' id='extra-option-contact-name'>
		<h3 class="list-title">Contact name:</h3>
		<g:textField name="contactString" value="${search?.contactString}"/>
		<a onclick="toggleExtraSearchOption('contact-name')"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
	</li>
	<g:each in="${customFieldList}" status="i" var="c" >
		<li class='field' id="extra-option-custom-field-${c.value}">
			<h3 class="list-title">${c.value}:</h3><br>
			<g:each in="${search?.customFields}" status="j" var="s">
				<g:if test="${s == c}">
					<g:textField name="${s.value}CustomField" value="${s.value}"/>
					<a onclick="toggleExtraSearchOption('custom-field-${c.value}')"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
				</g:if>
				<g:elseif test="${customFieldList.size() == i}">
					<g:textField name="${c.value}CustomField" value="${c.value}"/>
					<a onclick="toggleExtraSearchOption('custom-field-${c.value}')"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
				</g:elseif>
			</g:each>
			<g:if test="${!search?.customFields || search?.customFields == null}">
				<g:textField name="${c.value}CustomField" value="${c.value}"/>
				<a onclick="toggleExtraSearchOption('custom-field-${c.value}')"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
			</g:if>
		</li>
	</g:each>
</ul>

<h3 class="list-title">
	<a id="extra-options"><img src='${resource(dir:'images', file:'move-down.png')}' /></a>
	${message(code:'default.search.moresearchoption.label', default:'More search options') }
</h3>
<ul class="sub-list" id="expanded-search-options">
	<li>
		<a id="extra-option-link-contact-name" onclick="toggleExtraSearchOption('contact-name')">Contact name</a>
	</li>
	<g:each in="${customFieldList}" status="i" var="c" >
		<li>
			<a id="extra-option-link-custom-field-${c.name}" onclick="toggleExtraSearchOption('custom-field-${c.name}')">${c.name}</a>
		</li>
	</g:each>
</ul>
