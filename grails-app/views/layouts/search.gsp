<%@ page cntentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Search"/></title>
		<g:layoutHead />
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources theme="medium" plugin="randomtextosolvebug"/>
		<script type="text/javascript">
			url_root = "${request.contextPath}/";
			refresh_rate = ${params.rRate ?: 30000}
		</script>
		<g:javascript src="jquery.timers.js"/>
		<g:javascript src="application.js"/>
		<g:javascript src="search/moreOptions.js"/>
		<g:javascript src="message/check_message.js"/>
		<g:javascript src="message/arrow_navigation.js"/>
		<g:javascript src="/message/move_dropdown.js"/>
		<g:javascript src="message/star_message.js"></g:javascript>
		<g:javascript src="mediumPopup.js"/>
		<g:javascript src="smallPopup.js"/>
		<g:javascript src="pagination.js"/>
		<g:javascript src="/message/messageSorting.js"/>
		<g:javascript src="/message/moreActions.js"/>
		<g:javascript>
			$(function() {  
			   disablePaginationControls();
			});
		</g:javascript>
	</head>
	<body>
		<div id="container">
			<g:render template="/system_notifications"/>
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
			<g:render template="/flash"/>
			<div class="main">
				<g:render template="menu"/>
				<div class="content">
					<div id='search-header' class="content-header">
						<div id="search-title">
							<img src='${resource(dir:'images/icons',file:'search.png')}' />
							<h2>Search</h2>
			  			</div>
			  			<ol>
			  				<g:if test="${search}">
					  			<li id="export-btn">
						  			<g:remoteLink controller="export" action="wizard" params='[messageSection: "${messageSection}", searchId: "${search?.id}"]' onSuccess="launchSmallPopup('Export Results (${messageInstanceTotal} messages)', data, 'Export');">
										Export results
									</g:remoteLink>
								</li>
							</g:if>
							<g:else>
								<li id="export-btn">
						  			<a class="disabled">
										Export results
									</a>
								</li>
							</g:else>
						</ol>
						<g:if test="${searchDescription}">
							<div id="search-description">
								<p>
									${searchDescription}
						  		</p>
					  		</div>
					  	</g:if>
					</div>
					<div class="content-body">
						<g:render template="/message/message_list"/>
						<g:layoutBody />
					</div>
					<div class="content-footer">
							<ul id="filter">
								<li>Show:</li>
								<li><g:link action="${messageSection}" params="${params.findAll({it.key != 'starred' && it.key != 'offset'})}">All</g:link></li>
								<li>|</li>
								<li><g:link action="${messageSection}" params="${params.findAll({it.key != 'offset'}) + [starred: true]}" >Starred</g:link></li>
							</ul>
							<g:if test="${params.action != 'no_search'}">
								<div id="page-arrows">
									<g:paginate next="Forward" prev="Back"
										max="${grailsApplication.config.grails.views.pagination.max}"
										action="result" total="${messageInstanceTotal}" params= "${params.findAll({it.key != 'messageId'})}"/>
								</div>
							</g:if>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
