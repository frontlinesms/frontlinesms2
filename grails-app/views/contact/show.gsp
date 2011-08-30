<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta name="layout" content="contacts" />
		<g:javascript src="contact/show-groups.js"></g:javascript>
		<g:javascript src="contact/show-fields.js"></g:javascript>
		<script type="text/javascript">
			$(function() {
				$('input[name="name"]').focus();
			});
		</script>
    </head>
    <body>
    	<div id="contact_details">
    		<g:form name="details">
	    		<g:hiddenField name="version" value="${contactInstance?.version}"/>
	    		<g:hiddenField name="checkedContactList" id='checkedContactList' value="${params.checkedContactList}"/>
	    		<g:hiddenField name="groupsToAdd" value=","/>
				<g:hiddenField name="groupsToRemove" value=","/>
				<g:hiddenField name="fieldsToAdd" value=","/>
				<g:hiddenField name="fieldsToRemove" value=","/>
				<g:if test="${contactsSection instanceof frontlinesms2.Group}">
					<g:hiddenField name="groupId" value="${contactsSection.id}"/>
				</g:if>
				
	    		<g:render template="single_contact_view"/>
				<g:render template="multiple_contact_view"/>
			</g:form>
		</div>
    </body>
</html>
