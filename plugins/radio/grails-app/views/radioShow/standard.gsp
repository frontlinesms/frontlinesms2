<html>
	<head>
		<meta name="layout" content="${params.controller=='radioShow' ? 'messages' : 'archive'}"/>
		<r:require module="radio"/>
		<title>${pageTitle?:params.action.capitalize()}</title>
	</head>
	<body>
	</body>
</html>
