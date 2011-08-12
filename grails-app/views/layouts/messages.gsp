	<%@ page contentType="text/html;charset=UTF-8"%>
<html>
	<head>
		<title><g:layoutTitle default="Messages"/></title>
		<g:layoutHead/>
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon"/>
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
		<script type="text/javascript">
			url_root = "${request.contextPath}/";
		</script>
		<g:javascript src="message/check_message.js"/>
		<g:javascript src="/message/move_dropdown.js"/>
		<g:javascript src="message/star_message.js"></g:javascript>
		<g:javascript src="application.js"/>
		<g:javascript src="mediumPopup.js"/>
		<g:javascript src="smallPopup.js"/>
		<g:javascript src="/message/categorize-dropdown.js"/>
    </head>
	<body>
		<div id="container">
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
	        <g:render template="/flash"/>
	        <div class="main">
				<g:render template="menu"/>
				<div class="content">
					<div class="content-header ${messageSection}">
						<g:if test="${messageSection == 'poll'}">
							<div id="poll-title">
								<g:render template="poll_header"/>
							</div>
						</g:if>
						<g:elseif test="${messageSection == 'folder'}">
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'folders.gif')}' />
								<h2>${ownerInstance?.name}</h2>
							</div>
						</g:elseif>
						<g:elseif test="${messageSection == 'sent'}">
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'sent.gif')}' />
								<h2>${messageSection}</h2>
							</div>
						</g:elseif>
						<g:elseif test="${messageSection == 'pending'}">
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'pending.gif')}' />
								<h2>${messageSection}</h2>
							</div>
						</g:elseif>
						<g:elseif test="${messageSection == 'trash'}">
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'trash.gif')}' />
								<h2>${messageSection}</h2>
							</div>
						</g:elseif>
						<g:elseif test="${messageSection == 'radioShow'}">
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'onair.gif')}' />
								<h2>on air</h2>
							</div>
						</g:elseif>
						<g:else>
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'inbox.gif')}' />
								<h2>${messageSection}</h2>
							</div>
						</g:else>
						<ol>
							<li>
								<g:remoteLink controller="export" action="wizard" params='[messageSection: "${messageSection}", ownerId: "${ownerInstance?.id}", activityId: "${activityId}", searchString: "${searchString}", groupId: "${groupInstance?.id}"]' onSuccess="launchSmallPopup('Export', data, 'Export');">
									Export
								</g:remoteLink>
							</li>
							<li id="manage-subscription">
								<g:remoteLink controller="group" action="list" onSuccess="launchMediumWizard('Manage Subscription', data, 'Create');">
									Manage subscription
								</g:remoteLink>
							</li>
							<li>
					        	<g:remoteLink controller="quickMessage" action="create" onSuccess="launchMediumWizard('Quick Message', data, 'Send');" id="quick_message">
					        		<img src='${resource(dir:'images/icons',file:'quickmessage.gif')}' />
									Quick message
								</g:remoteLink>
							</li>
						</ol>
						<g:if test="${messageSection == 'poll'}">
							<g:if test="${!params.archived}">
								<g:link controller="poll" action="archive" id="${ownerInstance.id}">Archive Activity</g:link>
							</g:if>
							<button id="pollSettings">Show poll details</button>
							<div id="pollGraph"></div>
						</g:if>
					</div>
					<div class="content-body">
						<g:render template="list_items"/>
						<g:layoutBody />
					</div>
					<div class="content-footer">
							<ul id="filter">
								<li>Show:</li>
								<li><g:link action="${messageSection}" params="${params.findAll({it.key != 'starred' && it.key != 'max' && it.key != 'offset'})}">All</g:link></li>
								<li>|</li>
								<li><g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset'}) + [starred: true]}" >Starred</g:link></li>
							</ul>
							<div id="page-arrows">
								<g:paginate next="Next" prev="Back"
									max="${grailsApplication.config.pagination.max}"
									action="${messageSection}" total="${messageInstanceTotal}" params="${params.findAll({it.key != 'messageId'})}"/>
							</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
