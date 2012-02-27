<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings" />
		<title>Settings > System Logs</title>
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
				<div>You have no logs.</div>
			</g:if>
			<g:else>
				<div id="log-filters">
					<span>Show logs for</span>
					<span>
						<select class="dropdown" name="timePeriod" onChange="filterLogEntries(this.value)">
							<option value="forever">all time</option>
							<option value="1">last 24 hours</option>
							<option value="3">last 3 days</option>
							<option value="7">last 7 days</option>
							<option value="14">last 14 days</option>
							<option value="28">last 28 days</option>
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
