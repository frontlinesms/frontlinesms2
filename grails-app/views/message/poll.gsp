<html>
    <head>
        <title>Poll</title>
		<meta name="layout" content="messages" />
		<g:javascript library="jquery" plugin="jquery"/>
		<g:javascript library="jquery" plugin="jquery"/>
		<g:javascript src="/graph/raphael-min.js"/>
		<g:javascript src="/graph/g.raphael-min.js"/>
		<g:javascript src="/graph/g.bar-min.js"/>
		<g:javascript src="/graph/graph.js"/>
		<g:javascript>
		$(function() {
			var loaded = false;
			var show = true;
			$("#poll-graph-btn").click(function() {
				if(!loaded)
				{
					var xdata = $.map(${pollResponse}, function(a) {return a.value;});
					var data =  $.map(${pollResponse}, function(a) {return a.count;});
					var responseCountTag= "<span class='response-count'>${messageInstanceTotal} responses total</span>"
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
		</g:javascript>
		<g:javascript>
		$(function() {
			var pollDisplay = $("#poll-graph-btn");
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
		<div id="poll-details" style="display:none">
			<div id="pollGraph"></div>
		</div>
	</body>
</html>

