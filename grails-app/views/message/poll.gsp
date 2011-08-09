<html>
    <head>
        <meta name="layout" content="messages" />
        <title>Poll</title>
		<g:render template="expanded_poll" />
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
		<a id="pollSettings" class="show-arrow" href="#">Show poll details</a><div id="pollGraph"></div>
    </body>
</html>

