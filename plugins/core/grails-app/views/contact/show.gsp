<%@ page import="frontlinesms2.Contact" %>
<html>
	<head>
		<g:if test="${pageTitle != null}">
			<title>${pageTitle}</title>
		</g:if>
		<meta name="layout" content="contacts"/>
		<script type="text/javascript">
			$(function() {
				$('input[name="name"]').focus();
			});
		</script>
	</head>
    <body>
    	<div id="contact-editor">
    		<g:form name="details">
	    		<g:hiddenField name="version" value="${contactInstance?.version}"/>
	    		<g:hiddenField name="checkedContactList" id='checkedContactList' value=","/>
	    		<g:hiddenField name="groupsToAdd" value=","/>
				<g:hiddenField name="groupsToRemove" value=","/>
				<g:hiddenField name="fieldsToAdd" value=","/>
				<g:hiddenField name="fieldsToRemove" value=","/>
				<g:hiddenField name="sort" value="${params.sort}"/>
				<g:hiddenField name="offset" value="${params.offset}"/>
				<g:hiddenField name="groupId" value="${contactsSection?.id}"/>
				<g:hiddenField name="contactsSection" value="${contactsSection instanceof frontlinesms2.Group ? 'group' : 'smartGroup'}"/>
				<g:if test="${contactInstance}">
					<g:hiddenField name="contactId" value="${contactInstance?.id}"/>
				</g:if>
	    		<fsms:render template="single_contact_view"/>
				<fsms:render template="multiple_contact_view"/>
			</g:form>
		</div>
    </body>
</html>
