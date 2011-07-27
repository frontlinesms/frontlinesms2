<g:javascript src="raphael-min.js"/>
<g:javascript src="g.raphael-min.js"/>
<g:javascript src="g.bar-min.js"/>
<g:javascript src="graph.js"/>
<g:javascript>
$(function() {
		var data1 =  [40, 28, 18, 20, 10, 30, 15, 30, 18, 48, 205, 0, 20, 15];
		var data2 = [30, 18, 48, 25, 5, 20, 15, 40, 28, 18, 20, 0, 30, 15];
		var xdata = ["05/05", "06/05", "07/05", "08/05", "09/05", "10/05", "11/05", "12/05", "13/05", "14/05", "15/05", "16/05", "17/05", "18/05"];
		var data = [data1, data2];
		var dataCaption = ["Sent", "Received"];
		var holder = "trafficGraph";
		var r = Raphael(holder);
		var c = r.plotStackedBarGraph(holder, data, xdata, dataCaption, {colors : ["#D4D5D6", "#949494"], textStyle: {"font-weight": "bold", "font-size": 12}, 
		padding : {left: 40, top: 20, bottom: 20, right: 50 }});
	});
</g:javascript>
Traffic
<div id="trafficGraph" style="width:650px;height:400px"></div>