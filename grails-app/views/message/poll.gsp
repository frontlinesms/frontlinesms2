<html>
    <head>
        <title>Poll</title>
		<g:render template="expanded_poll" />
		<meta name="layout" content="${actionLayout}" />
		<g:javascript library="jquery" plugin="jquery"/>
		<g:javascript src="raphael-min.js"/>
		<g:javascript src="g.raphael-min.js"/>
		<g:javascript src="g.bar-min.js"/>
		<g:javascript src="graph.js"/>
		<g:javascript>
		$(function() {
			var pollDisplay = $("#pollSettings");
			pollDisplay.click(function() {
				if (pollDisplay.html() == "Hide poll details") {
					pollDisplay.html("Show poll details");
					pollDisplay.addClass("show-arrow");
					pollDisplay.removeClass("hide-arrow");
				} else {
					pollDisplay.html("Hide poll details");
					pollDisplay.addClass("hide-arrow");
					pollDisplay.removeClass("show-arrow");
				}
			});
		});
		</g:javascript>	
	</head>
	<body>
		<g:if test="${messageInstance != null}">
			<g:render template="message_details" />
		</g:if>
	</body>
</html>

