<%@ page import="frontlinesms2.Fconnection" %>
<ul id="tab-nav">
        <li id="message-tab" class="tab ${params.controller == 'message' ? 'current' : ''}">
            <g:link id="message-tab-link" class="tab-link" url="${[controller:'message']}">
               <span id="message-tab-text"><g:message code="tabs.messages"/></span>
			<span id="inbox-indicator" class="">${frontlinesms2.Fmessage.countUnreadMessages()}</span>
            </g:link>
        </li>
    
        <li class="tab ${params.controller == 'archive' ? 'current' : ''}">
            <g:link class="tab-link" controller='archive' action="inbox"><g:message code="tabs.archive"/></g:link>
        </li>

        <li class="tab ${params.controller == 'contact' ? 'current' : ''}">
            <g:link class="tab-link" url="${[controller:'contact']}" id="tab-contacts"><g:message code="tabs.contacts"/></g:link>
        </li>
    
        <li id="status-tab-button" class="tab ${params.controller == 'status' ? 'current' : ''}">
            <g:link class="tab-link" url="${[controller:'status']}" id="status-tab-link">
                <g:message code="tabs.status"/>
                <span id="status-indicator" class="indicator"></span>
            </g:link>
        </li>
    
        <li class="tab ${params.controller == 'search' ? 'current' : ''}">
            <g:link class="tab-link" url="${[controller:'search', action:'no_search']}" id="tab-search"><g:message code="tabs.search"/></g:link>
        </li>
    </ul>
    <div id="tabbar"></div>
