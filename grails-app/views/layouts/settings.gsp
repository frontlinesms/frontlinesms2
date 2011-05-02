<%--
  Created by IntelliJ IDEA.
  User: david
  Date: 4/19/11
  Time: 4:23 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Settings"/></title>
        <g:layoutHead />
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
	</head>
	<body>
		<g:render template="/eyebrow"/>
		<g:render template="/settings/menu"/>
		<g:layoutBody />
	</body>
</html>