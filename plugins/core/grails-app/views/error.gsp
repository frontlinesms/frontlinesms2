<html>
	<head>
		<title>FrontlineSMS :: Unhandled Exception</title>
		<style>
			body { margin-left:auto; margin-right:auto; width:600px; font-family:sans-serif; }
			textarea { width:100%; height:6em; }
			a,a:link,a:visited,a:active { color:blue; }
		</style>
	</head>

	<body>
		<h1>Sorry, an exception has occurred.  /o\ </h1>
		<g:set var="content">
--- Please include a description of your error here ---



--- Please include the following technical information so that we can debug the issue ---
	
Error ${request.'javax.servlet.error.status_code'}: ${request.'javax.servlet.error.message'}
Servlet: ${request.'javax.servlet.error.servlet_name'}
URI: ${request.'javax.servlet.error.request_uri'}
			<g:if test="${exception}">
Exception Message: ${exception.message}
Caused by: ${exception.cause?.message}
Class: ${exception.className}
At Line: [${exception.lineNumber}]
Code Snippet:
<g:each var="cs" in="${exception.codeSnippet}">${cs}</g:each>

--- Stacktrace ---
<g:each in="${exception.stackTraceLines}">${it}</g:each>
			</g:if>
		</g:set>
		<div>
			<p>Please report this problem so that we can improve our software!</p>
			<p>If your computer has a default email client you can click the "Send report" button to automatically open it and send us the report. We may also need your logs, so please download them and attach the resulting zip file as well.</p>
			<p>
				Otherwise please send us an email at
				<a href="mailto:support@frontlinesms.com?subject=Error%20Report-%20${exception.message}&body=${content}">support@frontlinesms.com</a>
				and include your logs by
				<g:link class="btn" controller="error" action="logs">downloading them</g:link>
				and attaching the resulting zip file.

				To provide more information, please include your logs and database by
				<g:link class="btn" controller="error" action="logsAndDatabase">downloading them</g:link>
				and attaching the resulting zip file.
			</p>
			<p>Thank you for your help! \o/</p>
			<p>
				Download 
				<g:link class="btn" controller="error" action="logs">logs</g:link> or 
				<g:link class="btn" controller="error" action="logsAndDatabase">logs and database</g:link>
			</p>
			<form action="mailto:support@frontlinesms.com?subject=Error%20Report-%20${exception.message}" method="post" enctype="text/plain">
				<textarea name="body">${content}</textarea><br/>
				<input id="send_email" type="submit" value="Send report to admin team"/>
			</form>
		</div>
	</body>
</html>
