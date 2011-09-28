<ol class="context-menu" id='search-menu'>
	<g:form name="search-details">
		<li id='search-field' class="section">
			<h2 id="search-string">Keyword or phrase:</h2>
			<ol class='sub-menu'>
				<li class='field'>
					<g:textField name="searchString" id="searchString" value="${search?.searchString}"/>
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
					<g:actionSubmit class="search" controller="search" action="result" name="result" value="${message(code: 'default.button.search.label', default: 'Search')}" />
				</li>
				<li>
					<g:link action="no_search">${message(code: 'default.search.label', default: 'Clear search')}</g:link>
				</li>
			</ol>
		</li>
	</g:form>
</ol>
