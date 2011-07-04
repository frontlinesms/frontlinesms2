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
		  <div id="export-results">
			<g:if test="${messageInstanceList}">
				 Export results as
				<export:formats  formats="['pdf','csv']" params="['searchString':params.searchString, 'groupId':params.groupId, 'activityId':params.activityId]" action="downloadReport" />
			</g:if>
			<g:else> Export results as PDF | CSV</g:else>
			
		  </div>
	  </h2>
  </body>
</html>