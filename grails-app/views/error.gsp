<html>
  <head>
	  <title>Grails Runtime Exception</title>
	  <style type="text/css">
	  		.message {
	  			border: 1px solid black;
	  			padding: 5px;
	  			background-color:#E9E9E9;
	  		}
	  		.stack {
	  			border: 1px solid black;
	  			padding: 5px;
	  			overflow:auto;
	  			height: 300px;
	  		}
	  		.snippet {
	  			padding: 5px;
	  			background-color:white;
	  			border:1px solid black;
	  			margin:3px;
	  			font-family:courier;
	  		}
	  </style>
  </head>

  <body>
    <h1>FrontlineSMS Exception</h1>
    <g:set var="LINE_BREAK" value="%0D%0A"/>
	<g:set var="foo">
			  Error ${request.'javax.servlet.error.status_code'}: ${request.'javax.servlet.error.message'.encodeAsHTML()} ${LINE_BREAK}
			  Servlet: ${request.'javax.servlet.error.servlet_name'} ${LINE_BREAK}
			  URI: ${request.'javax.servlet.error.request_uri'} ${LINE_BREAK}
			  <g:if test="${exception}">
				  Exception Message: ${exception.message?.encodeAsHTML()} ${LINE_BREAK}
				  Caused by: ${exception.cause?.message?.encodeAsHTML()} ${LINE_BREAK}
				  Class: ${exception.className} ${LINE_BREAK}
				  At Line: [${exception.lineNumber}] ${LINE_BREAK} 
			  </g:if>
	</g:set>


  <a href="mailto:support@frontlinesms.com?subject=Error report -
				${exception.message?.encodeAsHTML()}&body=${foo}">Send report to admin team</a>
  </body>
</html>