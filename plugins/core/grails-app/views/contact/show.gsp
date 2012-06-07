<%@ page import="frontlinesms2.Contact" %>
<html>
	<head>
		<title>${contactsSection ? "Contacts >> ${contactsSection.name}" : 'Contacts'}</title>
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
			<fsms:render template="single_contact_view"/>
			<fsms:render template="multiple_contact_view"/>
		</div>
	</body>
</html>
