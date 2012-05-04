<html>
	<head>
		<title>${pageTitle?:params.action.capitalize()}</title>
		<r:require module="radio"/>
		<meta name="layout" content="messages" />
		<g:javascript>
			$(function() {  
			   disablePaginationControls();
			});
		</g:javascript>
    </head>
    <body>
    </body>
</html>
