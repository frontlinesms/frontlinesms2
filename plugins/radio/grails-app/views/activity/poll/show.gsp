<html>
	<head>
		<title><g:message code="poll.title" args="${[ownerInstance.name]}"/></title>
		<meta name="layout" content="${params.controller=='message' ? 'messages' : 'archive'}"/>
		<r:require module="graph"/>
		<fsms:render template="/activity/poll/poll_graph_js" plugin="core"/>
	</head>
	<body>
		<div id="poll-details" style="display:none">
			<div id="pollGraph-container">
				<div id="pollGraph"></div>
			</div>
		</div>
		<fsms:render template="/wordcloud/wordcloud"/>
	</body>
</html>

