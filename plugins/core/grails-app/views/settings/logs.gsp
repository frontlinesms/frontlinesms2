<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title>Settings ><g:message code="settings.logs.header"/></title>
		<g:javascript>
			function filterLogEntries(timePeriod) {
				$.get(url_root + 'settings/logs', {timePeriod: timePeriod}, function(data) {
					$('#log-list').replaceWith($(data).find('#log-list'));
				});
			}
		</g:javascript>
	</head>
	<body>
		<div id='logEntries'>
			<g:if test="${logEntryTotal==0}">
				<div><g:message code="logs.none"/></div>
			</g:if>
			<g:else>
				<div id="log-filters">
					<span id="show-log-text"><g:message code="logs.filter.label"/></span>
					<span>
						<select class="dropdown" name="timePeriod" onChange="filterLogEntries(this.value)">
							<option value="forever"><g:message code="logs.filter.anytime"/></option>
							<option value="1"><g:message code="logs.filter.1day"/></option>
							<option value="3"><g:message code="logs.filter.3days"/></option>
							<option value="7"><g:message code="logs.filter.7days"/></option>
							<option value="14"><g:message code="logs.filter.14days"/></option>
							<option value="28"><g:message code="logs.filter.28days"/></option>
						</select>
					</span>
				</div>
				<br/>
				<div id="log-list">
					<table>
						<g:each in="${logEntryList}" var="l">
							<tr class="logEntry">
								<td class="entry-content">${l.content}</td>
								<td class="entry-date">${l.date}</td>
							</tr>
						</g:each>
					</table>
				</div>
			</g:else>
		</div>
	</body>
</html>
