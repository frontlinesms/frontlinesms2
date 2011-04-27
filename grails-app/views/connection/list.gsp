<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
	  <meta name="layout" content="connection" />
	  <title>Settings > Connections</title>

  </head>
	<body>
		<ol id='connections'>
			<g:each in="${connectionInstanceList}" status="i" var="c">
					<li id='item' class="${c == connectionInstance ? 'selected' : ''}">
						<g:link action="show" class="show" id="${c.id}">'${c.name}' (${c.type})</g:link>
					</li>
			</g:each>
		</ol>
		<ol id='createRouteButtons'>
			<g:each in="${connectionInstanceList}" status="i" var="c">
					<li id='item' class="${c == connectionInstance ? 'selected' : ''}">
						<g:link action="createRoute" class='route' id="${c.id}">Create route</g:link>
					</li>
			</g:each>
		</ol>
		<div id='btnNewConnection'>
			<g:link action='create'>Add new connection</g:link>
		</div>
	</body>
</html>