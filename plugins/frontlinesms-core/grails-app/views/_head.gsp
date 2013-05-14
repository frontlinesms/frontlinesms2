<%@ page import="frontlinesms2.Fconnection" %>
<div id="head">
	<!--[if lte IE 7]>
	 <p class="warning_message" style="z-index:11;"><g:message code="browser.notsupported.warning"/></p><br />
	<![endif]-->
	<div id="main-nav" style="z-index:-1;">
		<ul>
			<fsms:tab controller="message" mainNavSection="${mainNavSection}">
				<span id="inbox-indicator" class="">${frontlinesms2.Fmessage.countTotalUnreadMessages()}</span>
			</fsms:tab>
			<fsms:tab controller="archive" mainNavSection="${mainNavSection}"/>
			<fsms:tab controller="contact" mainNavSection="${mainNavSection}"/>
			<fsms:tab controller="status" mainNavSection="${mainNavSection}">
				<fsms:trafficLightStatus/>
			</fsms:tab>
			<fsms:tab controller="search" mainNavSection="${mainNavSection}"/>
		</ul>
	</div>
</div>

