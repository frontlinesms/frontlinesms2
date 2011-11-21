<div id="sidebar">
	<ul class="main-list" id='search-menu'>
		<g:form name="search-details">
			<li>
				<h3 id="search-string" class="list-title">Keyword or phrase:</h3>
				<ul class='sub-list'>
					<li class='field'>
						<g:textField name="searchString" id="searchString" value="${search?.searchString}"/>
					</li>
				</ul>
			</li>
			<li class='section' id="search-filters">
				<div id='filters'>
					<g:render template="basic_filters"/>
					<g:render template="other_filters"/>
				</div>
			</li>
			<li class='section buttons'>
				<ul class='sub-list'>
					<li>
						<g:actionSubmit class="btn search" controller="search" action="result" name="result" value="${message(code: 'default.button.search.label', default: 'Search')}" />
					</li>
					<li>
						<g:link action="no_search">${message(code: 'default.search.label', default: 'Clear search')}</g:link>
					</li>
				</ul>
			</li>
		</g:form>
	</ul>
</div>
