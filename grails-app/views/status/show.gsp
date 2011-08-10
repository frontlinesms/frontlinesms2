<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="status" />
    </head>
    <body>
		<g:render template="traffic" />
		<div class="status">
			<div>
				<div class="label"><img src="/frontlinesms2/images/${MTNDONGLE.getIndicator()}-status.png" />MTN Dongle</div>
				<div class="value">             
					<div>Connected</div>
					<div>Signal Strength</div>
					<div>Balance</div>
				</div>
			</div>

			<div>
				<div class="label"><img src="/frontlinesms2/images/${GMAIL.getIndicator()}-status.png" />GMail</div>
				<div class="value">
					<div>Connected</div>
					<div>Mail server responding</div>
				</div>
			</div>

			<div>
				<div class="label"><img src="/frontlinesms2/images/${INTERNET.getIndicator()}-status.png" />Internet</div>
				<div class="value">
					<div>Connected</div>
					<div>DNS server responding</div>
				</div>
			</div>

			<div>
				<div class="label"><img src="/frontlinesms2/images/${MESSAGEQUEUE.getIndicator()}-status.png" />Message Queue</div>
				<div class="value">
					<div>Message queue 15 pending SMS for 'MTN Dongle'</div>
					<div>0 pending results</div>
				</div>
			</div>
		</div>
    </body>
</html>

