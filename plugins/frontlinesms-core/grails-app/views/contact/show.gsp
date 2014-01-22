<%@ page import="frontlinesms2.Contact" %>
<html>
	<head>
		<title>
			<g:if test="${contactsSection == null}">
				<g:message code="contact.header"/>
			</g:if>
			<g:else>
				<g:message code="contact.header.group" args="[contactsSection.name]"/>
			</g:else>
		</title>
		<meta name="layout" content="contacts"/>
		<r:script>
			$(function() {
				$('input[name="name"]').focus();
			});
		</r:script>
	</head>
	<body>
		<div class="content">
			<g:hiddenField name="groupId" value="${contactsSection?.id}"/>
			<g:hiddenField name="contactsSection" value="${contactsSection instanceof frontlinesms2.Group ? 'group' : 'smartGroup'}"/>
			<g:if test="${contactInstance}">
				<fsms:render template="single_contact"/>
			</g:if>
			<g:else>
				<p class="no-content"><g:message code="contact.list.no.selected"/></p>	
			</g:else>
			<fsms:render template="multiple_contact"/>
		</div>
	</body>
</html>

