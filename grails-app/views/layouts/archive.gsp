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
		<g:javascript src="mediumPopup.js"/>
	</head>
	<body>
		<div id="container">
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
			<g:render template="/flash"/>
			<div class="main">
				<g:render template="../archive/menu"/>
				<div class="content">
					<div id='archive-header' class="content-header">
			  			<g:if test="${messageSection == 'poll'}">
			  				<div id="poll-title">
								<img src='${resource(dir:'images/icons',file:'activitiesarchive.png')}' />
								<h2>Poll Archive</h2>
							</div>
						</g:if>
						<g:elseif test="${messageSection == 'inbox'}">
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'inboxarchive.png')}' />
								<h2>${messageSection} Archive</h2>
							</div>
						</g:elseif>
						<g:elseif test="${messageSection == 'sent'}">
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'sentarchive.png')}' />
								<h2>${messageSection} Archive</h2>
							</div>
						</g:elseif>
						<g:elseif test="${messageSection == 'folder'}">
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'foldersarchive.png')}' />
								<h2>${messageSection} Archive</h2>
							</div>
						</g:elseif>
					</div>
					<div class="content-body">
						<g:if test="${messageSection == 'poll' && !viewingMessages}">
							<g:render template="archived_poll_list"/>
						</g:if>
						<g:elseif test="${messageSection == 'folder' && !viewingMessages}">
							<g:render template="archived_folder_list"/>
						</g:elseif>
						<g:else>
							<g:render template="../message/message_list"/>
						</g:else>
						<g:layoutBody />
					</div>
					<div class="content-footer">
						<g:if test="${(messageSection == 'poll' || messageSection == 'folder') && !viewingMessages}">
								<div id="page-arrows">
									<g:paginate next="Forward" prev="Back" max="${grailsApplication.config.grails.views.pagination.max}" action="${messageSection}" total="${itemInstanceTotal}" params= "${params.findAll({it.key != 'messageId'})}"/>
								</div>
						</g:if>
						<g:else>
							<ul id="filter">
								<li>Show:</li>
								<li><g:link action="${messageSection}" params="${params.findAll({it.key != 'starred' && it.key != 'max' && it.key != 'offset'})}">All</g:link></li>
								<li>|</li>
								<li><g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset'}) + [starred: true]}" >Starred</g:link></li>
							</ul>
							<div id="page-arrows">
								<g:paginate next="Forward" prev="Back" max="${grailsApplication.config.grails.views.pagination.max}" action="${messageSection}" total="${messageInstanceTotal}" params= "${params.findAll({it.key != 'messageId'})}"/>
							</div>
						</g:else>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
