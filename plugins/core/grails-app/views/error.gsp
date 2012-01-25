<html>
  <head>
	  <title>FrontlineSMS Exception</title>
  </head>

  <body>
    <h1>FrontlineSMS Exception</h1>
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
		<textarea name="body" cols="150" rows="20">${content}</textarea>
		<input type="submit" value="Send report to admin team"> 
	</form>
  </body>
</html>