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
		<g:javascript src="contact/checked_contact.js" />
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
		
			$(document).ready(function(){
				$('#group-actions').bind('change', function() {
					var selected = $(this).find('option:selected').val();
					if(selected)
						remoteHash[selected].call();
				});
			});
		</g:javascript>
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
							<div  id="contact-title">
								<g:hiddenField name="groupId" value="${contactsSection?.id}"/>
								<img src='${resource(dir:'images/icons',file:'groups.png')}' />
								<h2>${contactsSection.name}</h2>
							</div>
							<g:select name="group-actions" from="${['Rename group']}"
								keys="${['renameGroup']}"
								noSelection="${['': 'More actions...']}"/>
						</g:if>
						<g:elseif test="${!contactInstance}">
							<div  id="contact-title">
								<img src='${resource(dir:'images/icons',file:'groups.png')}' />
								<h2>New Group</h2>
							</div>
						</g:elseif>
						<g:else>
							<div  id="contact-title">
								<img src='${resource(dir:'images/icons',file:'contacts.png')}' />
								<h2>${contactInstance.name?:contactInstance.primaryMobile?:'New Contact'}</h2>
							</div>
						</g:else>
					</div>
					<div class="content-body">
						<g:render template="contact_list"/>
						<g:layoutBody />
					</div>
					<g:render template="footer"/>
				</div>
			</div>
		</div>
	</body>
</html>
