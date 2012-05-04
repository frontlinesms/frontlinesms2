<div id="sidebar">
	<ul class="main-list" id='search-menu'>
		<g:form name="search-details">
			<li>
				<h3 id="search-string" class="list-title"><g:message code="search.keyword.label"/></h3>
				<ul class='sub-list'>
					<li class='field'>
						<g:textField name="searchString" id="searchString" value="${search?.searchString}"/>
					</li>
				</ul>
			</li>
			<li class='section' id="search-filters">
				<fsms:render template="basic_filters"/>
				<fsms:render template="other_filters"/>
			</li>
			<li class='section buttons' id="search-btn" >
				<ul class='sub-list'>
					<li>
						<g:actionSubmit class="btn create" controller="search" action="result" name="result" value="${message(code: 'default.button.search.label', default: 'Search')}"/>
					</li>
					<li>
						<g:link id="clear" action="no_search">${message(code:'search.clear', default:'Clear search')}</g:link>
					</li>
				</ul>
			</li>
		</g:form>
	</ul>
</div>
