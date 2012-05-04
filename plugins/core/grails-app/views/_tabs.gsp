<%@ page import="frontlinesms2.Fconnection" %>
<ul id="tab-nav">
        <li>
            <g:link class="tab-link ${params.controller == 'message' ? 'current' : ''}" url="${[controller:'message']}"	id="message-tab-link">
                <g:message code="tabs.messages"/>
				<span id="inbox-indicator" class="indicator">${frontlinesms2.Fmessage.countUnreadMessages()}</span>
            </g:link>
        </li>
    
        <li>
            <g:link class="tab-link ${params.controller == 'archive' ? 'current' : ''}" controller='archive' action="inbox"><g:message code="tabs.archive"/></g:link>
        </li>

        <li>
            <g:link class="tab-link ${params.controller == 'contact' ? 'current' : ''}" url="${[controller:'contact']}" id="tab-contacts"><g:message code="tabs.contacts"/></g:link>
        </li>
    
        <li>
            <g:link class="tab-link ${params.controller == 'status' ? 'current' : ''}" url="${[controller:'status']}" id="status-tab-link">
                <g:message code="tabs.status"/>
                <span id="status-indicator" class="indicator"></span>
            </g:link>
        </li>
    
        <li>
            <g:link class="tab-link ${params.controller=='search'?'current':''}" url="${[controller:'search', action:'no_search']}" id="tab-search"><g:message code="tabs.search"/></g:link>
        </li>
    </ul>
    <div id="tabbar"></div>
