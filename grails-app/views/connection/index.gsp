<%--
  Created by IntelliJ IDEA.
  User: david
  Date: 4/19/11
  Time: 6:58 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head><meta name="layout" content="connection" /></head>
	<body>
		<ol id='connections'>
			<g:each in="${connectionInstanceList}" status="i" var="c">
				<li class="${c == connectionInstance ? 'selected' : ''}"><g:link action="show" id="${c.id}">${c.name}</g:link></li>
			</g:each>
		</ol>
		<div id='btnNewConnection'>
			<g:link action='create'>Add new connection</g:link>
		</div>
	</body>
</html>