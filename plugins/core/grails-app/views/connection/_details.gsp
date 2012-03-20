<%@ page import="frontlinesms2.*" %>
<div id="tabs-2">
	<g:if test="${!fconnectionInstance || fconnectionInstance instanceof frontlinesms2.SmslibFconnection}">
		<div id="smslib-form">
			<h2>${g.message(code:'smslibfconnection.label')}</h2>
			<fsms:inputs instance="${fconnectionInstance}" instanceClass="${SmslibFconnection}"
					fields="name, port, baud, pin, smsc, imsi, serial"/>
		</div>
	</g:if>
	<g:if test="${!fconnectionInstance || fconnectionInstance instanceof frontlinesms2.EmailFconnection}">
		<div id="email-form">
			<h2>${g.message(code:'emailfconnection.label')}</h2>
			<fsms:inputs instance="${fconnectionInstance}" instanceClass="${EmailFconnection}"
					fields="name, receiveProtocol, serverName, serverPort, username, password"/>
		</div>
	</g:if>
	<g:if test="${!fconnectionInstance || fconnectionInstance instanceof frontlinesms2.ClickatellFconnection}">
		<div id="clickatell-form">
			<h2>${g.message(code:'clickatellfconnection.label')}</h2>
			<fsms:inputs instance="${fconnectionInstance}" instanceClass="${ClickatellFconnection}"
					fields="name, apiId, username, password"/>
		</div>
	</g:if>
</div>
