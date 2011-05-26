<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="contacts" />
        <g:set var="entityName" value="${message(code: 'contact.label', default: 'Contact')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
		<script type="text/javascript" language="javascript">
			$(function() {
			    $('input[name="name"]').focus();
			});
		</script>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:form name="contact-details" action="saveContact" >
                <div class="dialog">
                    <table>
                        <tbody>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name"><g:message code="contact.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: contactInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${contactInstance?.name}"/>
                                </td>
                            </tr>
							<tr class="prop">
                                <td valign="top" class="name">
                                    <label for="address"><g:message code="contact.address.label" default="Address" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: contactInstance, field: 'address', 'errors')}">
                                    <g:textField name="address" value="${contactInstance?.address}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>


				<div id="buttons">
                    <g:submitButton name="createContact" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
					<g:link class="cancel" action="list">Cancel</g:link>
                </div>


            </g:form>
        </div>
    </body>
</html>
