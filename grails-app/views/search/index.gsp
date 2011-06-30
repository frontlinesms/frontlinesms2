<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
	  <meta name="layout" content="search" />
  </head>
  <body>
	  <h2 id="search-description">
		  ${searchDescription}
	  </h2>
	  <h2>
		  Export to <export:formats  formats="['pdf','csv']" params="['searchString':params.searchString, 'groupId':params.groupId, 'activityId':params.activityId]" action="downloadReport"/>
	  </h2>
  </body>
</html>