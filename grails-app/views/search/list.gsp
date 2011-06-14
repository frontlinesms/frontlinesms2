<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
	  <meta name="layout" content="search" />
  </head>
  <body>
	  <h2 id="search-description">
		  <g:if test="${keywords}">Searching in
				<g:if test="${groupInstance != null && pollInstance != null}">'${groupInstance.name}' and '${pollInstance.title}'</g:if>
				<g:elseif test="${groupInstance != null}">'${groupInstance.name}'</g:elseif>
				<g:elseif test="${pollInstance != null}">'${pollInstance.title}'</g:elseif>
				<g:else>all messages</g:else>
		  </g:if>
		  <g:else>
			  Start new search on the left
		  </g:else>
	  </h2>
  </body>
</html>