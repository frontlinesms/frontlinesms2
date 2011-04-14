
<%@ page import="frontlinesms2.Contact" %>
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
				<div id="address">
					<label for="address"><g:message code="contact.address.label" default="Address"/></label>
					<g:textField name="address" id="address" value="${contactInstance?.address}"/>
				</div>
			</div>
			<div class="buttons">
				<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
			</div>
		</g:form>
    </body>
</html>
