<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Archive"/></title>
		<g:layoutHead />
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources theme="medium" plugin="randomtextosolvebug"/>
		<script type="text/javascript">
			url_root = "${request.contextPath}/";
			refresh_rate = ${params.rRate ?: 30000}
		</script>
		<g:javascript src="message/check_message.js"></g:javascript>
		<g:javascript src="message/star_message.js"></g:javascript>
		<g:javascript src="jquery.timers.js"/>
		<g:javascript src="application.js"/>
		<g:javascript src="jquery.ui.selectmenu.js"/>
		<g:javascript src="mediumPopup.js"/>
		<g:javascript src="pagination.js"/>
		<g:javascript src="/message/messageSorting.js"/>
		<g:javascript>
			$(function() {  
			   disablePaginationControls();
			});
		</g:javascript>
	</head>
	<body>
		<g:render template="/system_notifications"/>
		<div id="header">
			<img id="logo" src="/frontlinesms2/images/logo.png">
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
			<g:render template="/flash"/>
        	</div>
		<div id="main" class="main">
    			<g:render template="../archive/menu"/>
				<div id="content" class="content">
					<div id='archive-header' class="content-header section-header">
						<div id="archive-title">
				  			<g:if test="${messageSection in ['poll', 'announcement']}">
								<h3>Activity Archive</h3>
							</g:if>
							<g:elseif test="${messageSection == 'inbox'}">
								<h3>${messageSection} Archive</h3>
							</g:elseif>
							<g:elseif test="${messageSection == 'sent'}">
								<h3>${messageSection} Archive</h3>
							</g:elseif>
							<g:elseif test="${messageSection == 'folder'}">
								<h3>${messageSection} Archive</h3>
							</g:elseif>
							<g:render template="../message/section_action_buttons"/>
						</div>
					</div>
					<div class="content-body">
						<g:if test="${(messageSection == 'poll' || messageSection == 'announcement') && !viewingMessages}">
							<g:render template="archived_activity_list"/>
						</g:if>
						<g:elseif test="${messageSection == 'folder' && !viewingMessages}">
							<g:render template="archived_folder_list"/>
						</g:elseif>
						<g:else>
							<g:render template="../message/message_list"/>
						</g:else>
						<g:layoutBody />
					</div>
					<g:if test="${(messageSection == 'inbox' || messageSection == 'sent' || viewingMessages)}">
						<g:render template="../message/footer"/>
					</g:if>
				</div>
			</div>
	</body>
</html>
