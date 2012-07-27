<html>
	<head>
		<title><g:message code="poll.title" args="${[ownerInstance.name]}"/></title>
		<meta name="layout" content="${params.controller=='message' ? 'messages' : 'archive'}"/>
		<r:require module="graph"/>
		<g:render template="/activity/poll/poll_graph_js"/>
	</head>
	<body>
		<div id="poll-details" style="display:none">
			<div id="pollGraph"></div>
		</div>
	</body>
</html>

