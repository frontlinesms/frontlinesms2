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
		<g:javascript src="jquery.timers.js"/>
		<g:javascript src="application.js"/>
		<g:javascript src="mediumPopup.js"/>
		<g:javascript src="smallPopup.js"/>
		<g:javascript src="pagination.js"/>
		<g:javascript src="contact/changingNumberAlert.js"/>
		<g:javascript src="contact/buttonStates.js" />
		<g:javascript src="contact/checked_contact.js" />
		<g:javascript src="contact/moreGroupActions.js" />
		<g:javascript>
			function getGroupId(){
				var group = $('#groupId');
				return group.length ? group.val() : '';
			}
			function updateContacts(data) {
				var replacement = $(data);
				$("#contact-list").html(replacement.filter('#contact-list').html());
				$(".footer #paging").html(replacement.filter('.footer #paging').html());
				disablePaginationControls();
			}

			$(function() {  
			   disablePaginationControls();
			   $("#contact-search").renderDefaultText();
			});
		</g:javascript>
		<g:render template="/css" />
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
