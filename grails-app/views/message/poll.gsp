<%@ page import="frontlinesms2.Contact" %>
<html>
	<head>
		<meta name="layout" content="messages" />
		<g:javascript library="jquery" plugin="jquery"/>
		<g:javascript src="raphael-min.js"/>
		<g:javascript src="g.raphael-min.js"/>
		<g:javascript src="g.bar-min.js"/>
		<g:javascript src="graph.js"/>
		<g:javascript>
		$(function() {
			var loaded = false;
			$("#pollSettings").click(function() {
				if(!loaded)
				{
					var xdata = $.map(${pollResponse}, function(a) {return a.value;});
					var data =  $.map(${pollResponse}, function(a) {return a.count;});
					var holder = "pollGraph";
					$("#"+holder).width(400);
					$("#"+holder).height(320);
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
		<title>Poll</title>
	</head>
	<body>
		<g:if test="${messageInstance != null}">
			<g:render template="message_details" />
		</g:if>
		<h2 id="poll-title">${ownerInstance?.title}</h2>
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
