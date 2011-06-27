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
    <h2>Error Details</h2>
	<a href="mailto:support@frontlinesms.com?subject=Error report -
				${exception.message?.encodeAsHTML()}&body=${request.'javax.servlet.error.message'.encodeAsHTML()}
				at line [${exception.lineNumber}]">Send report to admin team</a>
  </body>
</html>