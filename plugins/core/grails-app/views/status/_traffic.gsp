<g:javascript src="/graph/raphael-min.js"/>
<g:javascript src="/graph/g.raphael-min.js"/>
<g:javascript src="/graph/g.bar-min.js"/>
<g:javascript src="/graph/graph.js"/>
<g:javascript>
$(function() {
		var data1 = ${messageStats["sent"]};
		var data2 = ${messageStats["received"]};
		var xdata = ${messageStats["xdata"]};
		var data = [data1, data2];
		var dataCaption = ["Sent", "Received"];
		var holder = "trafficGraph";
		var r = Raphael(holder);
		var padding = {left: 40, top: 20, bottom: 20, right: 55 };
		var textStyle = {"font-weight": "bold", "font-size": 12};
		var c = r.plotStackedBarGraph(holder, data, xdata, dataCaption, {colors : ["#D4D5D6", "#949494"], textStyle: textStyle, 
		padding : padding, ystep:5});
		var sent =  data1.sum(), received = data2.sum(), total = data.pack().sum();
		var sentPercent = "", receivedPercent = "";
		if(total > 0) {
			sentPercent = " (" + Math.round(sent * 100 / total) + "%) ";
			receivedPercent = " (" + Math.round(received * 100 / total) + "%) ";
		} 
		var summary = r.text(r.width/2, r.height- padding.bottom, 
			i18n("traffic.sent")+": " + sent +  sentPercent +
			i18n("traffic.received")+": " + receivedPercent +
			i18n("traffic.total")+": " +  total)
			.attr(textStyle);

	});
</g:javascript>
<h3 id="traffic-title"><g:message code="traffic.header" /></h3>
<div id="traffic-status">
	<div id="trafficGraph"></div>
	<g:form action="show" method="post" name="trafficForm">
		<g:render template="../status/filters" plugin="core"/>
		<g:actionSubmit id="update-chart" value="${g.message(code:'traffic.update.chart')}" action="show"/>
	</g:form>
</div>
