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
				var snippet = $(data);
				$("#contacts-list").html(snippet.filter('#contacts-list').html());
				$(".content-footer #page-arrows").html(snippet.filter('.content-footer').children()[1].innerHTML);
				disablePaginationControls();
			}

			$(function() {  
			   disablePaginationControls();
			   $("#contact-search").renderDefaultText();
			});
		</g:javascript>
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
	</head>
	<body id="contacts-tab">
		<g:render template="/system_notifications"/>
		<div id="header">
			<img src="/frontlinesms2/images/logo.png" id="logo"/>
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
			<g:render template="/flash"/>
		</div>
		<div id="main">
			<g:render template="menu"/>
			<div id="content">
				<g:render template="header"/>
				<g:layoutBody />
			</div>
			<g:render template="footer"/>
		</div>
	</body>
</html>
