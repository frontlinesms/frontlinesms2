<%@ page import="frontlinesms2.Group" %>
<fsms:step type="join" stepId="${stepId}">
	<g:select name="group" id=""
			noSelection="${['null':'Select One...']}"
			from="${Group.getAll()}"
			value="${groupId}"
			optionKey="id" optionValue="name"
			class="notnull"/>
</fsms:step>

