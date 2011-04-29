<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
	  <meta name="layout" content="connection" />
	  <title>Settings > Connections</title>

  </head>
	<body>
		<g:if test="${fconnectionInstanceTotal==0}">
			<div id='connections'>You have no connections configured.</div>
		</g:if>
		<g:else>
			<ol id='connections'>
				<g:each in="${connectionInstanceList}" status="i" var="c">
						<li id='item' class="${c == connectionInstance ? 'selected' : ''}">
							<g:link action="show" class="show" id="${c.id}">
								<h2>${c.name}</h2>
								<h3>${c.type()}</h3>
							</g:link>
							<div class="buttons">
								<g:link action="createRoute" class='route' id="${c.id}">Create route</g:link>
							</div>
						</li>
				</g:each>
			</ol>
		</g:else>
		<div id='btnNewConnection'>
			<g:link action='create'>Add new connection</g:link>
		</div>
	</body>
</html>
