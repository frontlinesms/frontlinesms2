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
		</script>
		<g:javascript src="message/check_message.js"></g:javascript>
		<g:javascript src="message/star_message.js"></g:javascript>
		<g:javascript src="application.js"/>
		<g:javascript src="mediumPopup.js"/>
    </head>
	<body>
		<div id="container">
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
	        <g:render template="/flash"/>
	        <div class="main">
				<div class="content">
					<div id='archive-header' class="content-header">
						<div id="archive-title">
							<h2>Archives</h2>
			  			</div>
					</div>
					<div class="content-body">
						<g:render template="../archive/menu"/>
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
							<g:if test="${params.action == 'results'}">
								<div id="page-arrows">
									<g:paginate next="Forward" prev="Back"
										 max="${grailsApplication.config.pagination.max}"
										action="${messageSection}" total="${messageInstanceTotal}" params= "${params.findAll({it.key != 'messageId'})}"/>
								</div>
							</g:if>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
