
<%@ page import="frontlinesms2.Fconnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'fconnection.label', default: 'Fconnection')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
          		<div class="message">${flash.message}</div>
			</g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'fconnection.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="camelAddress" title="${message(code: 'fconnection.camelAddress.label', default: 'Camel Address')}" />
                        
                        	<th>Commands</th>
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${fconnectionInstanceList}" status="i" var="fconnectionInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${fconnectionInstance.id}">${fieldValue(bean: fconnectionInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: fconnectionInstance, field: "camelAddress")}</td>
                        
                        	<td>[ <g:link action="createRoute" id="${fconnectionInstance.id}">create route</g:link> ]</td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${fconnectionInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
