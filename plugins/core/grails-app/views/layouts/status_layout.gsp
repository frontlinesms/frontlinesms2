<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title><g:layoutTitle default="Status"/></title>
		<g:layoutHead/>
		<r:require module="status"/>
		<fsms:render template="/includes"/>
		<fsms:i18nBundle/>
		<r:layoutResources/>
	</head>
	<body id="content">
		<fsms:render template="/head"/>
		<div id="body" class="status">
			<div id="content">
				<g:layoutBody/>
			</div>
		</div>
		<fsms:render template="/system"/>
	</body>
</html>

