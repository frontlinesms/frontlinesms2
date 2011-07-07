<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Search"/></title>
		<g:layoutHead />
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<g:javascript library="jquery" plugin="jquery"/>
	</head>
	<body>
		<g:render template="/tabs"/>
		<g:render template="/flash"/>
		<div id="main">
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
			<g:layoutBody/>
			<g:render template="search_menu"/>
			<g:render template="/message/message_list"/>
		</div>
	</body>
</html>
