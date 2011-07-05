<%--
  Created by IntelliJ IDEA.
  User: david
  Date: 4/19/11
  Time: 12:54 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="contacts" />
    </head>
    <body>
		<g:form name="contactDetails">
			<g:hiddenField name="id" value="${contactInstance?.id}"/>
			<g:hiddenField name="version" value="${contactInstance?.version}"/>
			<div id="contactinfo">
				<div id="name">
					<label for="name"><g:message code="contact.name.label" default="Name"/></label>
					<g:textField name="name" id="name" value="${contactInstance?.name}"/>
				</div>
				<div id="primaryMobile">
					<label for="primaryMobile"><g:message code="contact.primaryMobile.label" default="Mobile"/></label>
					<g:textField name="primaryMobile" id="primaryMobile" value="${contactInstance?.primaryMobile}"/>
				</div>
			</div>
			<div class="buttons">
				<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
			</div>
		</g:form>
    </body>
</html>
