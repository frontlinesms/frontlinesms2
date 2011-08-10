<div id="top">
	<div id="logo"><img src='${resource(dir:'images',file:'logo_radio.png')}' width="36" height="40"/></div>
	<ul id="global-nav">
		<li>
			<g:link class="tab ${['message','folder','poll'].contains(params.controller)?'selected':''}" url="${[controller:'message']}"	id="tab-messages">Messages ${frontlinesms2.Fmessage.countUnreadMessages()}</g:link>
		</li>
		<li> 
			<g:link class="tab ${params.controller=='archive'?'selected':''}" url="${[controller:'archive']}">Archives</g:link>
		</li>
		<li>
			<g:link class="tab ${params.controller=='contact'?'selected':''}" url="${[controller:'contact']}" id="tab-contacts">Contacts</g:link>
		</li>
		<li>
			<g:link  class="tab ${params.controller=='search'?'selected':''}" url="${[controller:'search']}" id="tab-search">Search</g:link>
		</li>
	</ul>
</div>
<script>
	$.ajax({
		type:'GET',
		url: url_root + 'status/trafficLightIndicator',
		success: function(data){$('#indicator').attr("src", "../images/status_" + data + ".gif") }
	});
</script>
