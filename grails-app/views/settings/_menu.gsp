<%--
  Created by IntelliJ IDEA.
  User: david
  Date: 4/19/11
  Time: 12:58 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
		<ol id="settings-menu">
			<li><g:link action="general">General settings</g:link></li>
			<li><g:link url="${[controller:'connection', action:'index']}">Phones & connections</g:link></li>
		</ol>