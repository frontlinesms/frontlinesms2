<html>
	<head>
		<title><g:message code="poll.header"/></title>
		<meta name="layout" content="${params.controller=='message' ? 'messages' : 'archive'}"/>
		<r:require module="graph"/>
		<r:script>
		$(function() {
			var loaded = false;
			$("#poll-graph-btn").on("click", function(){
				
				var show = true;
				var pollGraphBtn = $("#poll-graph-btn");
				if (pollGraphBtn.html() == i18n("fmessage.hidepolldetails")) {
					pollGraphBtn.html(i18n("fmessage.showpolldetails"));
					pollGraphBtn.addClass("show-arrow");
					pollGraphBtn.removeClass("hide-arrow");
				} else {
					pollGraphBtn.html(i18n("fmessage.hidepolldetails"));
					pollGraphBtn.addClass("hide-arrow");
					pollGraphBtn.removeClass("show-arrow");
				}
				if (!loaded) {
					var xdata = $.map(${pollResponse}, function(a) {return a.value;});
					var data =  $.map(${pollResponse}, function(a) {return a.percent;});
					$("#poll-details").toggle();
					var holder = "pollGraph";
					var colors = ["#40B857", "#F2202B", "#ff9600"];
					loaded = true;
					plot3 = $.jqplot(holder, [data], {
							seriesColors: colors,
						    captureRightClick: true,
						    seriesDefaults:{
						      renderer:$.jqplot.BarRenderer,
						      rendererOptions: {
						          barMargin: 15,
						          varyBarColor : true,
						          highlightMouseDown: true   
						      },
						      pointLabels: {show: true}
						    },
						    axes: {
						      xaxis: {
						          renderer: $.jqplot.CategoryAxisRenderer,
						          ticks: xdata,
						      },
						      yaxis:{
									ticks:[0, 100],
									tickOptions:{formatString:'%d\%'}
								}

						    },
						    grid: {
						    	background: 'transparent'
							},
					  });
				}
				else{
						$("#poll-details").toggle();
				}
				$('#messages').toggle();
				$(".footer").toggle();
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

