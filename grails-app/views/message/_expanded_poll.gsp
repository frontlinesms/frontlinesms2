<g:javascript library="jquery" plugin="jquery"/>
<g:javascript src="raphael-min.js"/>
<g:javascript src="g.raphael-min.js"/>
<g:javascript src="g.bar-min.js"/>
<g:javascript src="graph.js"/>
<g:javascript>
$(function() {
	var loaded = false;
	var show = true;
	$("#pollSettings").click(function() {
		if(!loaded)
		{
			var xdata = $.map(${pollResponse}, function(a) {return a.value;});
			var data =  $.map(${pollResponse}, function(a) {return a.count;});
			var holder = "pollGraph";
			$("#"+holder).width($("#pollGraph").width);
			$("#"+holder).height($("#pollGraph").height);
			var r = Raphael(holder);
			r.plotBarGraph(holder, data, xdata, {
				colors: ["#949494", "#F2202B", "#40B857"],
				textStyle: {
					"font-weight": "bold",
					"font-size": 12
				}
			});
			loaded = true;
		}
		else
			$("#pollGraph").toggle();		
	});
});
</g:javascript>