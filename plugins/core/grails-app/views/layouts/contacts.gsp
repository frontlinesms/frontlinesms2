<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
		<g:layoutHead />
		<g:render template="/includes"/>
		<g:javascript src="contact/validateNumber.js"/>
		<g:javascript src="contact/buttonStates.js" />
		<g:javascript src="contact/checked_contact.js" />
		<g:javascript src="contact/moreGroupActions.js" />
		<g:javascript src="contact/search_within_list.js" />
	</head>
	<body id="contacts-tab">
		<div id="header">
			<div id="notifications">
				<g:render template="/system_notifications"/>
				<g:render template="/flash"/>
			</div>
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
		</div>
		<div id="main">
			<g:render template="menu"/>
			<div id="content">
				<g:render template="header"/>
				<g:layoutBody />
				<g:render template="footer"/>
			</div>
		</div>
	</body>
</html>
