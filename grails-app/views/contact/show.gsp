
<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'contact.label', default: 'Contact')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
		<style>
			.selected {
				background-color: green;
			}
		</style>
    </head>
    <body>
		Contact we are showing: ${contactInstance.name}

        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
			<g:if test="${contactInstanceTotal > 0}">
				<ol id="contacts">
					<g:each in="${contactInstanceList}" status="i" var="c">
						<li class="${c == contactInstance ? 'selected' : ''}"><g:link action="show" id="${c.id}">${c.name}</g:link></li>
					</g:each>
				</ol>
			</g:if>
			<g:else>
				<div id="contacts">
					You have no contacts saved
				</div>
			</g:else>
			 <g:form name="contactDetails">
				<g:hiddenField name="id" value="${contactInstance?.id}" />
                <g:hiddenField name="version" value="${contactInstance?.version}" />
				 <div id="contactinfo">
						<div id="name">
							<label for="name"><g:message code="contact.name.label" default="Name" /></label>
								<g:textField name="name" id="name" value="${contactInstance?.name}" />
						</div>
						<div id="address">
							<label for="address"><g:message code="contact.address.label" default="Address" /></label>
								<g:textField name="address" id="address" value="${contactInstance?.address}" />
						</div>
				 </div>
				 <div class="buttons">
                 	<span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
				 </div>
			 </g:form>





            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="contact.id.label" default="Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: contactInstance, field: "id")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="contact.address.label" default="Address" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: contactInstance, field: "address")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="contact.name.label" default="Name" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: contactInstance, field: "name")}</td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${contactInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
