<html>
	<head>
		<title><g:message code="poll.header"/></title>
		<meta name="layout" content="${params.controller=='message' ? 'messages' : 'archive'}"/>
		<r:require module="graph"/>
		<r:script>
		$(function() {
			$("#poll-graph-btn").on("click", function(){
			var loaded = false;
			var show = true;
				if (!loaded) {
					var xdata = $.map(${pollResponse}, function(a) {return a.value;});
					var data =  $.map(${pollResponse}, function(a) {return a.count;});
					var responseCountTag= "<span class='response-count'><g:message code="fmessage.responses.total" args="${ [messageInstanceTotal] }"/></span>"
					$("#poll-details").toggle();
					var holder = "pollGraph";
					$("#"+holder).width($("#pollGraph").width);
					$("#"+holder).height($("#pollGraph").height);
					$("#poll-details").prepend(responseCountTag);
					var formatString = '<table class="jqplot-highlighter">'
					formatString += '<tr><td>%s</td><td>&nbsp;messages</td></tr>'
					formatString += '</table>'
					var colors = ["#F2202B", "#40B857"];
					plot3 = $.jqplot(holder, [data], {
							seriesColors: colors,
						    captureRightClick: true,
						    seriesDefaults:{
						      renderer:$.jqplot.BarRenderer,
						      rendererOptions: {
						          // Put a 15 pixel margin between bars.
						          barMargin: 15,
						          // Highlight bars when mouse button pressed.
						          // Disables default highlighting on mouse over.
						          highlightMouseDown: true   
						      },
						      pointLabels: {show: false}
						    },
						    axes: {
						      xaxis: {
						          renderer: $.jqplot.CategoryAxisRenderer,
						          ticks: xdata
						      },
						      yaxis: {
						        padMin: 0
						      }
						    },
						    legend: {
								show: true,
								location: 'nw',
								placement: 'inside'
						    },
						    grid: {
						    	background: 'transparent'
							},
							highlighter: {
						    	show:true,
						    	showTooltip: true,
						    	tooltipLocation: 'n',
						    	tooltipAxes: 'y',
						    	yvalues: 1,
						    	formatString: formatString
							},
							legend: {
								renderer: $.jqplot.EnhancedLegendRenderer,
								show: true,
								location: 'nw',
								placement: 'inside'
						    }  
					  });
				}
				else{
						$("#poll-details").toggle();
				}
				$('#messages').toggle();
				$(".footer").toggle();
			});
			/*
			var pollDisplay = $("#poll-graph-btn");
			pollDisplay.live("click", function() {
				if (pollDisplay.html() == i18n("fmessage.hidepolldetails")) {
					pollDisplay.html(i18n("fmessage.showpolldetails"));
					pollDisplay.addClass("show-arrow");
					pollDisplay.removeClass("hide-arrow");
				} else {
					pollDisplay.html(i18n("fmessage.hidepolldetails"));
					pollDisplay.addClass("hide-arrow");
					pollDisplay.removeClass("show-arrow");
				}
			}); */
		});
		</r:script>	
	</head>
	<body>
		<div id="poll-details" style="display:none">
			<div id="pollGraph"></div>
		</div>
	</body>
</html>

