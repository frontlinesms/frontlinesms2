<div class="frontlinesync-edit-form">
	<p><g:message code="frontlinesync.info-setup.p1"/></p>
	<br/>
	<p><g:message code="frontlinesync.info-setup.p2"/></p>
	<br/>
	<div class="input-item">
		<label><g:message code="frontlinesync.name.label"/></label>
		<%
			def connectionsCount = frontlinesms2.FrontlinesyncFconnection.countByNameLike("%FrontlineSync%")
			def suffix = connectionsCount?" ($connectionsCount)" :''
			def connectionName = fconnectionInstance?.name?:'FrontlineSync'+ suffix
		%>
		<g:textField name="frontlinesyncname" value="${connectionName}"/>
	</div>
	<fsms:frontlineSyncPasscode connection="${fconnectionInstance}"/>
	<br/>
        <a target="_blank" class="google-play-link" href="https://play.google.com/store/apps/details?id=com.simlab.frontlinesync">
		<g:message code="fconnection.frontlinesync.googleplaystore.download"/>
	</a>
        <br/>
	<a target="_blank" class="google-play-link" href="https://play.google.com/store/apps/details?id=com.simlab.frontlinesync">
		<img alt="Android app on Google Play"
			src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
	</a>
</div>
