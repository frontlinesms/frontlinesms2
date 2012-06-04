<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title><g:layoutTitle default="Status"/></title>
		<g:layoutHead/>
		<r:require module="status"/>
		<fsms:render template="/includes"/>
		<fsms:i18n keys="traffic.sent, traffic.received, traffic.total, popup.cancel, popup.back, smallpopup.cancel, popup.help.title, popup.done, popup.ok"/>
		<r:layoutResources/>
	</head>
	<body id="content">
		<div id="head">
			<fsms:render template="/tabs"/>
		</div>
		<div id="body" class="status">
			<div id="content">
				<g:layoutBody/>
			</div>
		</div>
		<fsms:render template="/system"/>
	</body>
</html>

