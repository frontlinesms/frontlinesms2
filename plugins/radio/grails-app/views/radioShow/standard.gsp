<html>
	<head>
		<meta name="layout" content="${params.inArchive? 'archive' : 'messages'}"/>
		<title>${pageTitle?:params.action.capitalize()}</title>
	</head>
	<body>
		<fsms:render template="/wordcloud/wordcloud"/>
	</body>
</html>
