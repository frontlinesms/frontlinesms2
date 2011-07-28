<g:javascript src="raphael-min.js"/>
<g:javascript src="g.raphael-min.js"/>
<g:javascript src="g.bar-min.js"/>
<g:javascript src="graph.js"/>
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
		padding : padding});
		var sent =  data1.sum(), received = data2.sum(), total = data.pack().sum();
		var summary = r.text(r.width/2, r.height- padding.bottom, 
			"Sent: " + sent +  " (" + Math.round(sent * 100 / total) + "%) " +
			"Received: " + received + " (" + Math.round(received * 100 / total) + "%) " +
			"Total: " +  total)
			.attr(textStyle);
	});
</g:javascript>
Traffic
<div id="trafficGraph"></div>
<g:form action="show" method="post">
	<g:render template="../search/filters" />
	<g:datePicker name="startDate" value="${new Date()-14}" noSelection="['':'-Choose-']" precision="day"/>
	<g:datePicker name="endDate" value="${new Date()}" noSelection="['':'-Choose-']" precision="day"/>
	<div class="buttons">
		<g:actionSubmit id="update-chart" value="Update chart" action="show"/>
	</div>
</g:form>