<ol class="context-menu" id='search-menu'>
	<g:form name="search-details">
		<li id='search-field' class="section">
			<h2 id="search-string">Keyword or phrase:</h2>
			<ol class='sub-menu'>
				<li class='field'>
					<g:textField name="searchString" id="searchString" value="${searchString}"/>
				</li>
			</ol>
		</li>
		<li class='section' id="search-filters">
			<div id='filters'>
				<g:render template="basic_filters"/>
				<g:render template="other_filters"/>
			</div>
		</li>
		<li class='section buttons'>
			<ol class='sub-menu'>
				<li>
					<g:actionSubmit class="search" name="result" value="${message(code: 'default.button.search.label', default: 'Search')}" action="result"/>
				</li>
			</ol>
		</li>
	</g:form>
</ol>
