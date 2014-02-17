<%@ page import="frontlinesms2.Fconnection" %>
<div id="head">
	<div id="main-nav">
		<ul>
			<fsms:tab controller="message" mainNavSection="${mainNavSection}" selectedOverride="${params.controller=='missedCall'}">
				<span id="inbox-indicator" class="${frontlinesms2.TextMessage.countTotalUnreadMessages() == 0 ? 'allRead':''}">${frontlinesms2.TextMessage.countTotalUnreadMessages()}</span>
			</fsms:tab>
			<fsms:tab controller="archive" mainNavSection="${mainNavSection}"/>
			<fsms:tab controller="contact" mainNavSection="${mainNavSection}"/>
			<fsms:tab controller="connection" mainNavSection="${mainNavSection}">
				<fsms:trafficLightStatus/>
			</fsms:tab>
			<fsms:tab controller="search" mainNavSection="${mainNavSection}"/>
		</ul>
	</div>
</div>

