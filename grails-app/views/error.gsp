<html>
  <head>
	  <title>FrontlineSMS Exception</title>
  </head>

  <body>
    <h1>FrontlineSMS Exception</h1>
    <g:set var="LINE_BREAK" value="%0D%0A"/>
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


  <a href="mailto:support@frontlinesms.com?subject=Error report -
				${exception.message?.encodeAsHTML()}&body=${content}">Send report to admin team</a>
  </body>
</html>