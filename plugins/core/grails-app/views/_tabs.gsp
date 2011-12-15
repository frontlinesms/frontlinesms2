<%@ page import="frontlinesms2.Fconnection" %>
<div id="primary-nav" class="standard-nav">
    <ul>
        <li>
            <g:link class="tab-link ${params.controller == 'message' ? 'current' : ''}" url="${[controller:'message']}"	id="message-tab-link">
                Messages <span id="inbox-indicator" class="indicator">${frontlinesms2.Fmessage.countUnreadMessages()}</span>
            </g:link>
        </li>
    
        <li>
            <g:link class="tab-link ${params.controller == 'archive' ? 'current' : ''}" controller='archive' action="inbox">Archive</g:link>
        </li>

        <li>
            <g:link class="tab-link ${params.controller == 'contact' ? 'current' : ''}" url="${[controller:'contact']}" id="tab-contacts">Contacts</g:link>
        </li>
    
        <li>
            <g:link class="tab-link ${params.controller == 'status' ? 'current' : ''}" url="${[controller:'status']}" id="status-tab-link">
                Status
                <span id="status-indicator" class="indicator"></span>
            </g:link>
        </li>
    
        <li>
            <g:link class="tab-link ${params.controller=='search'?'current':''}" url="${[controller:'search']}" id="tab-search">Search</g:link>
        </li>
    </ul>
</div>
<div id="tabbar"></div>
<script>
	$.get(url_root + 'status/trafficLightIndicator', function(data) {
			var imageRoot = "${resource(dir:'images/icons', file:'status_')}";
			$('#indicator').replaceWith("<img id='indicator' src='" + imageRoot + data + ".png'/>");
	});
</script>
