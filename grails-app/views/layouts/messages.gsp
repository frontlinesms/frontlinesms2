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
			refresh_rate = ${params.rRate ?: 30000}
		</script>
		<g:javascript src="message/check_message.js"/>
		<g:javascript src="message/arrow_navigation.js"/>
		<g:javascript src="/message/move_dropdown.js"/>
		<g:javascript src="message/star_message.js" />
		<g:javascript src="jquery.timers.js"/>
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
						<g:hiddenField name="starred" value="${params.starred}" />
						<g:hiddenField name="viewingArchive" value="${params.viewingArchive}" />
						<g:hiddenField name="failed" value="${params.failed}" />
						<g:if test="${messageSection == 'poll'}">
							<div id="poll-title">
								<g:render template="../message/poll_header"/>
							</div>
						</g:if>
						<g:elseif test="${messageSection == 'folder'}">
							<div class="message-title">
								<g:if test="${params.viewingArchive}">
									<g:link controller="archive" action="folder">&lt; Back</g:link>
								</g:if>
								<g:else>
									<img src='${resource(dir:'images/icons',file:'folders.png')}' />
								</g:else>
								<h2>${ownerInstance?.name}</h2>
							</div>
						</g:elseif>
						<g:elseif test="${messageSection == 'sent'}">
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'sent.png')}' />
								<h2>${messageSection}</h2>
							</div>
						</g:elseif>
						<g:elseif test="${messageSection == 'pending'}">
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'pending.png')}' />
								<h2>${messageSection}</h2>
							</div>
						</g:elseif>
						<g:elseif test="${messageSection == 'trash'}">
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'trash.png')}' />
								<h2>${messageSection}</h2>
							</div>
						</g:elseif>
						<g:elseif test="${messageSection == 'radioShow'}">
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'onair.png')}' />
								<h2>on air</h2>
							</div>
						</g:elseif>
						<g:else>
							<div class="message-title">
								<img src='${resource(dir:'images/icons',file:'inbox.png')}' />
								<h2>${messageSection}</h2>
							</div>
						</g:else>
						<ol>
							<g:if test="${messageSection == 'trash' && messageInstance != null}">
								<li>
									<select id="trash-actions">
										<option value="na" class="na">Trash actions...</option>
										<option id="empty-trash" value="empty-trash" onclick="launchEmptyTrashConfirmation();">Empty trash</option>
									</select>
								</li>
							</g:if>
							<g:if test="${messageSection != 'trash' && messageSection != 'poll'}">
								<li>
									<g:link elementId="export" url="#">
										Export
									</g:link>
								</li>
							</g:if>
							<g:if test="${messageSection == 'folder' && !params.viewingArchive}">
								<li class='static_btn'>
									<g:link controller="folder" action="archive" id="${ownerInstance.id}">Archive Folder</g:link>
								</li>
							</g:if>
							<li>
					        	<g:remoteLink controller="quickMessage" action="create" onSuccess="launchMediumWizard('Quick Message', data, 'Send', null, true); addTabValidations();" id="quick_message">
					        		<img src='${resource(dir:'images/icons',file:'quickmessage.png')}' />
									Quick message
								</g:remoteLink>
							</li>
						</ol>
						<g:if test="${messageSection == 'poll'}">
							<g:if test="${!params.viewingArchive}">
								<ol>
									<li class='static_btn'>
										<g:link controller="poll" action="archive" id="${ownerInstance.id}">Archive Poll</g:link>
									</li>
								</ol>
							</g:if>
							<ol>
								<li>
									<g:select name="poll-actions" from="${['Export', 'Rename activity']}"
											keys="${['export', 'renameActivity']}"
											noSelection="${['': 'More actions...']}"/>
								</li>
							</ol>
							<ol>
								<li>
									<button id="pollSettings">Show poll details</button>
								</li>
							</ol>
							<div class="poll-details" style="display:none">
								<div id="pollGraph"></div>
							</div>
						</g:if>
					</div>
					<div class="container" style="display:block">
						<div class="content-body">
							<g:render template="../message/message_list"/>
							<g:layoutBody />
						</div>
						<div class="content-footer">
							<ul id="filter">
								<li>Show:</li>
								<li><g:link action="${messageSection}" params="${params.findAll({it.key != 'starred' && it.key != 'failed' && it.key != 'max' && it.key != 'offset'})}">All</g:link></li>
								<li>|</li>
								<li>
								<g:if test="${messageSection == 'pending'}">
									<g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset'}) + [failed: true]}" >Failed</g:link>
								</g:if>
								<g:else>
									<g:link action="${messageSection}" params="${params.findAll({it.key != 'max' && it.key != 'offset'}) + [starred: true]}" >Starred</g:link>
								</g:else>
								</li>
							</ul>
							<div id="page-arrows">
								<g:paginate next="Next" prev="Back"
									max="${grailsApplication.config.grails.views.pagination.max}"
									action="${messageSection}" total="${messageInstanceTotal}" params="${params.findAll({it.key != 'messageId'})}"/>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
<g:javascript>
	$("#poll-actions").bind('change', function() {
		var selected = $(this).find('option:selected').val();
		if(selected)
			remoteHash[selected].call();
	});

	$("#export").click(function() {
		remoteHash['export'].call();
	});

	if($(".prevLink").size() == 0) {
		$("#page-arrows").prepend('<a href="#" class="prevLink disabled">Back</a>');
	}

	if($(".nextLink").size() == 0) {
		$("#page-arrows").append('<a href="#" class="nextLink disabled">Back</a>');
	}
</g:javascript>
