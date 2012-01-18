<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources theme="medium" plugin="randomtextosolvebug"/>
		<script type="text/javascript">
			url_root = "${request.contextPath}/";
			refresh_rate = ${params.rRate ?: 30000}
		</script>
		<g:layoutHead />
		<g:javascript src="jquery.ui.selectmenu.js"/>
		<g:javascript src="application.js"/>
		<g:javascript src="mediumPopup.js"/>
		<g:javascript src="smallPopup.js"/>
		<g:javascript src="pagination.js"/>
		<g:javascript src="contact/validateNumber.js"/>
		<g:javascript src="contact/buttonStates.js" />
		<g:javascript src="contact/checked_contact.js" />
		<g:javascript src="contact/moreGroupActions.js" />
		<g:javascript src="contact/search_within_list.js" />
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
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
