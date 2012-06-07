<%@ page import="frontlinesms2.Fconnection" %>
<div id="head">
	<div id="main-nav">
		<ul>
			<fsms:tab controller="message">
				<span id="inbox-indicator" class="">${frontlinesms2.Fmessage.countUnreadMessages()}</span>
			</fsms:tab>
			<fsms:tab controller="archive"/>
			<fsms:tab controller="contact"/>
			<fsms:tab controller="status">
				<fsms:trafficLightStatus/>
			</fsms:tab>
			<fsms:tab controller="search"/>
		</ul>
	</div>
</div>

