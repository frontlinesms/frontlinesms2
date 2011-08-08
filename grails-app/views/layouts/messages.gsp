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
		<g:javascript src="message/actions.js"/>
		<g:javascript src="application.js"/>
		<g:javascript src="mediumPopup.js"/>
		<g:javascript src="smallPopup.js"/>
		<g:javascript src="/message/move_dropdown.js"/>
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
					<div id='poll-header' class="content-header">
						<g:if test="${messageSection == 'poll'}">
							<g:render template="poll_header"/>
						</g:if>
						<g:elseif test="${messageSection == 'folder'}">
							<h2 id="message-title">${ownerInstance?.name}</h2>
						</g:elseif>
						<g:else>
							<h2 id="message-title">${messageSection}</h2>
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
									Quick message
								</g:remoteLink>
							</li>
						</ol>
					</div>
					<div class="content-body">
						<g:render template="message_list"/>
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
