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
    		<g:render template="single_contact_view"/>
			<g:render template="multiple_contact_view"/>
		</div>
    </body>
</html>
