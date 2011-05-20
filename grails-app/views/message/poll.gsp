<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta name="layout" content="messages" />
        <title>Poll</title>
    </head>
    <body>
	  <h2 id="poll-title">${pollInstance.title}</h2>
	  <table id="poll-stats">
		<tbody>
			<g:each in="${pollResponseList.sort { it.value } }" status="i" var="r">
				<tr>
					<td>
						${r.value}
					</td>
					<td>
						${r.messages.size()}
					</td>
					<td>
						(${r.messages.size()/messageInstanceTotal*100}%)
					</td>
				</tr>
			</g:each>
		</tbody>
    </body>
</html>