<html>
	<head>
		<title><g:message code="poll.header"/></title>
		<meta name="layout" content="${params.controller=='message' ? 'messages' : 'archive'}"/>
		<r:require module="graph"/>
		<r:script>
		$(function() {
			var loaded = false;
			var show = true;
			$("#poll-graph-btn").click(function() {
				if (!loaded) {
					var xdata = $.map(${pollResponse}, function(a) {return a.value;});
					var data =  $.map(${pollResponse}, function(a) {return a.count;});
					var responseCountTag= "<span class='response-count'><g:message code="fmessage.responses.total" args="${ [messageInstanceTotal] }"/></span>"
					$("#poll-details").toggle();
					var holder = "pollGraph";
					$("#"+holder).width($("#pollGraph").width);
					$("#"+holder).height($("#pollGraph").height);
					$("#poll-details").prepend(responseCountTag);
					var r = Raphael(holder);
					r.plotBarGraph(holder, data, xdata, { // TODO find an alternative to putting this style info here - should be set via CSS class
						colors: ["#949494", "#F2202B", "#40B857"],
						textStyle: {
							"font-weight": "bold",
							"font-size": 12
						}
					});
					loaded = true;
				}
				else
					$("#poll-details").toggle();
				$('#messages').toggle();
				$(".footer").toggle();
			});
		});
		</r:script>
		<r:script>
		$(function() {
			var pollDisplay = $("#poll-graph-btn");
			pollDisplay.click(function() {
				if (pollDisplay.html() == i18n("fmessage.hidepolldetails")) {
					pollDisplay.html(i18n("fmessage.showpolldetails"));
					pollDisplay.addClass("show-arrow");
					pollDisplay.removeClass("hide-arrow");
				} else {
					pollDisplay.html(i18n("fmessage.hidepolldetails"));
					pollDisplay.addClass("hide-arrow");
					pollDisplay.removeClass("show-arrow");
				}
			});
		});
		</r:script>	
	</head>
	<body>
		<div id="poll-details" style="display:none">
			<div id="pollGraph"></div>
		</div>
	</body>
</html>

