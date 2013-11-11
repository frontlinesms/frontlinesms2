<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="settings.logs.header"/></title>
		<r:script>
			function filterLogEntries(timePeriod) {
				$.get(url_root + 'settings/logs', {timePeriod: timePeriod}, function(data) {
					$('#log-list').replaceWith($(data).find('#log-list'));
				});
			}
		</r:script>
	</head>
	<body>
		<div id="body-content-head" class="logs">
			<div class="content">
				<h1><g:message code="settings.logs.header"/></h1>
				<fsms:render template="/settings/log_buttons"/>
			</div>
		</div>
		<table id="body-content" class="logs">
			<thead>
				<tr>
					<th class="content"><g:message code="logs.content"/></th>
					<th class="date"><g:message code="logs.date"/></th>
				</tr>
			</thead>
			<tbody>
				<g:if test="${logEntryTotal==0}">
					<tr class="no-content"><td colspan="2"><g:message code="logs.none"/></td></tr>
				</g:if>
				<g:else>
					<g:each in="${logEntryList}" var="l">
						<tr>
							<td class="content">${l.content}</td>
							<td class="date">${l.date}</td>
						</tr>
					</g:each>
				</g:else>
			</tbody>
		</table>
	</body>
</html>

