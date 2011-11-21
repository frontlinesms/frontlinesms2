<html>
    <head>
        <title>Poll</title>
		<g:render template="../message/expanded_poll" />
		<meta name="layout" content="messages" />
		<g:javascript library="jquery" plugin="jquery"/>
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
	</body>
</html>

