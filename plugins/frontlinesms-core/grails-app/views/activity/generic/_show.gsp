<html>
	<head>
		<meta name="layout" content="${params.controller=='message'|| params.controller=='search'? 'interactions': 'archive'}"/>
		<title><g:message code="${ownerInstance.shortName}.title" args="${[ownerInstance.name]}"/></title>
		<g:hiddenField name="activityType" value="${ownerInstance.shortName}"/>
	</head>
	<body>
	</body>
</html>

