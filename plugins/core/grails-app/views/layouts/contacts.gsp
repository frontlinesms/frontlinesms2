<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
		<r:require module="contacts"/>
		<fsms:render template="/includes"/>
		<g:layoutHead/>
		<fsms:i18nBundle/>
		<r:script>
			$(function() {
				disablePaginationControls();
				$(window).resize(new Resizer('#main-list-container', '#main-list-head', '#main-list-foot'));
			});
		</r:script>
		<r:layoutResources/>
	</head>
	<body>
		<fsms:render template="/head"/>
		<div id="body" class="contacts">
			<div id="body-menu">
				<fsms:render template="menu"/>
			</div>
			<g:form>
				<div id="main-list-container">
					<div id="main-list-head">
						<fsms:render template="header"/>
					</div>
					<fsms:render template="contact_list"/>
					<div id="main-list-foot">
						<fsms:render template="footer"/>
					</div>
				</div>
				<div id="detail">
						<g:layoutBody/>
				</div>
			</g:form>
		</div>
		<fsms:render template="/system"/>
	</body>
</html>
