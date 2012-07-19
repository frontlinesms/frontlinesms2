<html>
	<head>
		<meta name="layout" content="${params.controller=='message' ? 'messages' : 'archive'}"/>
		<title>${pageTitle?:params.action.capitalize()}</title>
	</head>
	<body>
		<fsms:render template="/wordcloud/wordcloud"/>
	</body>
</html>
