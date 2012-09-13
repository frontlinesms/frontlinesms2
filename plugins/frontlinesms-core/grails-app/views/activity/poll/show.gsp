<html>
	<head>
		<title><g:message code="poll.title" args="${[ownerInstance.name]}"/></title>
		<meta name="layout" content="${params.controller=='message' ? 'messages' : 'archive'}"/>
		<r:require module="graph"/>
	</head>
	<body>
		<div id="poll-details" style="display:none">
			<div id="pollGraph-container">
				<div id="pollGraph"></div>
			</div>
		</div>
	</body>
</html>

<r:script>
$(function() {
	var pollGraph = new PollGraph(${pollResponse}, "${ownerInstance.id}", "${createLink(controller:'poll', action:'pollStats')}");
	$("#poll-graph-btn").live("click", pollGraph.show);
	setInterval(pollGraph.updateStats, 5000);
});
</r:script>

