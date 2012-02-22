<%@ page import="frontlinesms2.Fconnection" %>
<div id="primary-nav" class="standard-nav">
    <ul id='tab-list'>
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
            <g:link class="tab-link ${params.controller=='search'?'current':''}" url="${[controller:'search', action:'no_search']}" id="tab-search">Search</g:link>
        </li>
    </ul>
    <div id="tabbar"></div>
</div>

