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
		<title>Poll</title>
	</head>
	<body>
		<g:if test="${!params.archived}">
			<g:link controller="poll" action="archive" id="${ownerInstance.id}">Archive Activity</g:link>
		</g:if>
		<g:if test="${messageInstance != null}">
			<g:render template="message_details" />
		</g:if>
		<h2 id="poll-title">${ownerInstance?.title}</h2>
		    <div>${ownerInstance?.question}</div>
		    <div>${ownerInstance?.instruction}</div>
			<g:if test="$responseList">
			<table id="poll-stats">
				<tbody>
					<g:each in="${responseList}" var="r">
						<tr>
							<td>
								${r.value}
							</td>
							<td>
								${r.count}
							</td>
							<td>
								(${r.percent}%)
							</td>
						</tr>
					</g:each>
				</tbody>
			</table>
			<button id="pollSettings">Poll Settings</button>
		</g:if>
		<g:if test="${messageInstance}">
			<g:render  template="categorize_response"/>
		</g:if>
		<div id="pollGraph"></div>
	</body>
</html>

