<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources />
		<g:layoutHead />
		<g:javascript src="application.js"/>
		<g:javascript src="popup.js"/>
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
						<g:if test="${contactsSection instanceof frontlinesms2.Group}">
							<h2>${contactsSection.name}</h2>
						</g:if>
						<g:elseif test="${!contactInstance}">
							<h2>New Group</h2>
						</g:elseif>
						<g:else>
							<h2>${contactInstance.name?:contactInstance.primaryMobile?:'New Contact'}</h2>
						</g:else>
					</div>
					<div class="content-body">
						<g:render template="contact_list"/>
						<g:layoutBody />
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
