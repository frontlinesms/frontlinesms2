<div id="database-backup">
	<h2><g:message code="configuration.location.title"/></h2>
	<fsms:info message="configuration.location.description"/>
	<p><g:message code="configuration.location.instructions"/></p>
	<% def loc = frontlinesms2.ResourceUtils.resourcePath %>
	<input type="text" value="${loc}" size="${loc.size()}"/>
	<a href="file://${loc}"><g:message code="action.view"/></a>
</div>