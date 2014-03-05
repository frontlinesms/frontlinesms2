<html>
	<head>
		<title><g:message code="poll.title" args="${[ownerInstance.name]}"/></title>
		<meta name="layout" content="${params.controller=='message' || params.controller=='search' ? 'interactions' : 'archive'}"/>
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
	new PollGraph(${pollResponse}, "${ownerInstance.id}", "${createLink(controller:'poll', action:'pollStats')}");
});
</r:script>

