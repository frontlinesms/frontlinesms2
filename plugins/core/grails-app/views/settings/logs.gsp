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
					Show logs for
					<select class="dropdown" name="timePeriod" onChange="filterLogEntries(this.value)">
						<option value="forever">For all time</option>
						<option value="1">For last 24 hours</option>
						<option value="3">For last 3 days</option>
						<option value="7">For last 7 days</option>
						<option value="14">For last 14 days</option>
						<option value="28">For last 28 days</option>
					</select>
				</div>
				<br/>
				<div id="log-list">
					<ul>
						<g:each in="${logEntryList}" var="l">
							<li class="logEntry">
								<div>
									<span>${l.content}</span>
									<span>${l.date}</span>
								</div>
							</li>
						</g:each>
					</ul>
				</div>
			</g:else>
		</div>
	</body>
</html>
