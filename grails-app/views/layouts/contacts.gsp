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

		</g:javascript>
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
	</head>
	<body id="contacts-tab">
        <div id="header">
            <img src="/frontlinesms2/images/logo.png" id="logo"/>
		    <g:render template="/system_menu"/>
	    	<g:render template="/tabs"/>
    		<g:render template="/flash"/>
        </div>
	    <div id="main">
            <div id="sidebar">
		        <g:render template="menu"/>
            </div>
		    <div id="content">
				<div class="section-actions" id="contact-actions">
					<g:if test="${contactsSection instanceof frontlinesms2.Group}">
						<g:hiddenField name="groupId" value="&groupId=${contactsSection?.id}"/>
						<h3>${contactsSection.name}</h3>
                    </g:if>
					<g:elseif test="${!contactInstance}">
						<h3>New Group</h3>
					</g:elseif>
					<g:else>
						<h3>${contactInstance.name?:contactInstance.primaryMobile?:'New Contact'}</h3>
					</g:else>
				</div>
			    <div  id="contacts">
				    <g:layoutBody />
    			</div>
	        </div>
            <div style="clear:both;"></div>
	    </div>
        <g:render template="footer"/>
	</body>
</html>
