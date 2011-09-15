<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources theme="medium" plugin="randomtextosolvebug"/>
		<script type="text/javascript">
			url_root = "${request.contextPath}/";
		</script>
		<g:layoutHead />
		<g:javascript src="application.js"/>
		<g:javascript src="mediumPopup.js"/>
		<g:javascript src="smallPopup.js"/>
		<g:javascript src="contact/checked_contact.js"></g:javascript>
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
	</head>
	<body>
		<div id="container">
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
			<g:render template="/flash"/>
			<div class="main">
				<g:render template="menu"/>
				<div class="content">
					<div class="content-header">
						<div  id="contact-title">
							<g:if test="${contactsSection instanceof frontlinesms2.Group}">
								<img src='${resource(dir:'images/icons',file:'groups.png')}' />
								<h2>${contactsSection.name}</h2>
							</g:if>
							<g:elseif test="${!contactInstance}">
								<img src='${resource(dir:'images/icons',file:'groups.png')}' />
								<h2>New Group</h2>
							</g:elseif>
							<g:else>
								<img src='${resource(dir:'images/icons',file:'contacts.png')}' />
								<h2>${contactInstance.name?:contactInstance.primaryMobile?:'New Contact'}</h2>
							</g:else>
						</div>
					</div>
					<div class="content-body">
						<g:render template="contact_list"/>
						<g:layoutBody />
					</div>
					<div class="content-footer">
							<div id="page-arrows">
								<g:paginate next=" " prev=" "
									max="${grailsApplication.config.grails.views.pagination.max}"
									action="list" total="${contactInstanceTotal}" params="${params}"/>
							</div>
						</div>
				</div>
			</div>
		</div>
	</body>
</html>
<g:javascript>
	if($(".prevLink").size() == 0) {
		$("#page-arrows").prepend('<a href="#" class="prevLink disabled" disabled></a>');
	}

	if($(".nextLink").size() == 0) {
		$("#page-arrows").append('<a href="#" class="nextLink disabled" disabled></a>');
	}
</g:javascript>
