<r:require module="graph"/>
<r:script>
$(function() {
	var data1 = ${messageStats["sent"]};
	var data2 = ${messageStats["received"]};
	var xdata = ${messageStats["xdata"]};
	var data = [data1, data2];
	var dataCaption = ["Sent", "Received"];
	var holder = "trafficGraph";
	var sent =  data1.sum(), received = data2.sum(), total = data.pack().sum();
	var sentPercent = "", receivedPercent = "";
	if(total > 0) {
		sentPercent = " (" + Math.round(sent * 100 / total) + "%) ";
		receivedPercent = " (" + Math.round(received * 100 / total) + "%) ";
	}

	var formatString = '<table class="jqplot-highlighter">'
	formatString += '<tr><td>%s</td><td>&nbsp;messages</td></tr>'
	formatString += '</table>'
	plot3 = $.jqplot(holder, data, {
		stackSeries: true,
		captureRightClick: true,
		seriesDefaults:{
			renderer:$.jqplot.BarRenderer,
			rendererOptions:{ barMargin:15, highlightMouseDown:true },
			pointLabels: {show: false}
		},
		series:[
			{ label:i18n("traffic.sent") + ':' + sent + sentPercent },
			{ label:i18n("traffic.received") + ':' + received + receivedPercent }
		],
		axes: {
			xaxis: { renderer:$.jqplot.CategoryAxisRenderer, ticks:xdata },
			yaxis:{ padMin:0 }
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
	
	$(window).bind('resize', function(event, ui) {
		plot3.replot( { resetAxes: true } );
	});
});
</r:script>
<div id="traffic-status">
	<div id="trafficGraph" class="ui-widget-content ui-resizable"></div>
	<g:form action="show" method="post" name="trafficForm">
		<fsms:render template="/status/filters"/>
		<div id="filter-buttons">
			<g:actionSubmit class="btn" id="update-chart" value="${g.message(code:'traffic.update.chart')}" action="show"/>
			<g:link name="filter-reset" controller="status" action="index">
				<g:message code="traffic.filter.reset"/>
			</g:link>
		</div>
	</g:form>
</div>
