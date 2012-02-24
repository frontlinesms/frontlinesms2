<html>
  <head>
	  <title>FrontlineSMS Exception</title>
  </head>

  <body>
    <h1>Sorry, an exception has occurred.  /o\ </h1>
    <g:set var="LINE_BREAK" value=""/>
	<g:set var="content">
		Error ${request.'javax.servlet.error.status_code'}: ${request.'javax.servlet.error.message'.encodeAsHTML()} ${LINE_BREAK}
		Servlet: ${request.'javax.servlet.error.servlet_name'} ${LINE_BREAK}
		URI: ${request.'javax.servlet.error.request_uri'}${LINE_BREAK}
		<g:if test="${exception}">
			  Exception Message: ${exception.message?.encodeAsHTML()} ${LINE_BREAK}
			  Caused by: ${exception.cause?.message?.encodeAsHTML()} ${LINE_BREAK}
			  Class: ${exception.className} ${LINE_BREAK}
			  At Line: [${exception.lineNumber}] ${LINE_BREAK}
		</g:if>
	</g:set>
	<form action="mailto:support@frontlinesms.com?subject=Error%20Report-%20${exception.message?.encodeAsHTML()}" method="post" enctype="text/plain">
		<div>
			<p>Please report this error so that we can improve our software! If your computer has a default email client you can click the "Send report" button to attomatically open it and send us the report. We may also need your logs, so please download them and attach the resulting zip file as well.</p>
			<p>Otherwise please send us an email at <a href="mailto:support@frontlinesms.com">support@frontlinesms.com</a> and include your logs by clicking the "Download Logs" button and attaching the resulting zip file.<p>
			<p>Thank you for your help! \o/</p>
			<input id='send_email' type="submit" value="Send report to admin team"> 
			<g:link class="btn" controller="error" action="zip_to_download">Download logs</g:link>
		</div>
		<textarea name="body" cols="150" rows="20">${content}</textarea>
		</form>
  </body>
</html>
