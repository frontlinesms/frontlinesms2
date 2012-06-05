<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title>Settings ><g:message code="settings.logs.header"/></title>
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
				<ul class="buttons">
					<li>
						<label for="timePeriod" id="show-log-text"><g:message code="logs.filter.label" /></label>
						<select class="dropdown" name="timePeriod" onChange="filterLogEntries(this.value)">
							<option value="forever"><g:message code="logs.filter.anytime" /></option>
							<option value="1"><g:message code="logs.filter.1day" /></option>
							<option value="3"><g:message code="logs.filter.3days" /></option>
							<option value="7"><g:message code="logs.filter.7days" /></option>
							<option value="14"><g:message code="logs.filter.14days" /></option>
							<option value="28"><g:message code="logs.filter.28days" /></option>
						</select>
					</li>
					<li>
						<g:remoteLink class="btn" controller="settings" action="logsWizard" id="downloadLogs" onSuccess="launchSmallPopup(i18n('logs.download.title'), data, i18n('logs.download.continue'))">
							<g:message code="logs.download.buttontext"/>
						</g:remoteLink>
					</li>
				</ul>
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
					<tr><td colspan="2"><g:message code="logs.none"/></td></tr>
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

