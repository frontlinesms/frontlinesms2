<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Messages"/></title>
		<g:layoutHead />
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources />
		<g:javascript src="application.js"/>
    </head>
	<body>
		<g:render template="/tabs"/>
		<g:render template="quick_message"/>
		<g:remoteLink controller="quickMessage" action="create" onSuccess="loadContents(data);" class="quick_message">
			Quick Message
		</g:remoteLink>
        <g:render template="/flash"/>
		<div id="main">
			<g:render template="menu"/>
			<g:render template="message_list"/>
			<g:layoutBody/>
			Show:
			<g:link action="${messageSection}" params="${[starred:true] << params}" >Starred</g:link>
			<g:link action="${messageSection}" params="${params.findAll({it.key != 'starred'})}">All</g:link>
		</div>
	</body>
</html>
