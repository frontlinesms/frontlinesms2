<%@ page contentType="text/html;charset=UTF-8" %>
<g:form name="searchDetails" class="content-menu">
	<div id="search-details" >
		<div class="field">
			<label for="searchString">Keyword or phrase:</label>
			<g:textField name="searchString" id="searchString" value="${searchString}"/>
		</div>
	<g:render template="filters" />
	</div>
	<div class="buttons">
		<g:actionSubmit class="search" name="result" value="${message(code: 'default.button.search.label', default: 'Search')}" action="result"/>
	</div>
</g:form>