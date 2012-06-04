<div id="body-menu" class="search">
	<g:form name="search-details">
		<div class="input">
			<label for="searchString"><g:message code="search.keyword.label"/></label>
			<g:textField name="searchString" id="searchString" value="${search?.searchString}"/>
		</div>
		<div class="section" id="search-filters">
			<fsms:render template="basic_filters"/>
			<fsms:render template="other_filters"/>
		</div>
		<g:actionSubmit class="btn" controller="search" action="result" value="${message(code:'default.button.search.label')}"/>
		<g:link id="clear" action="no_search" class="btn"><g:message code="search.clear"/></g:link>
	</g:form>
</div>

