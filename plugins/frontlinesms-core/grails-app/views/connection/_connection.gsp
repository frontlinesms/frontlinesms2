<g:link action="show" id="${c.id}">
	<div class="connection-header" id="connection-${c?.id}">
		<h2>'${c.name}'</h2>
		<p class="connection-type">(<g:message code="${c.shortName}.label"/>)</p>
		<!-- FIXME should not have connection-specific code in this view.  if it's necessary we sould have separate templates
			for each.
			TODO 'your-ip-addres' should be 18n'd -->
		<g:if test="${c instanceof frontlinesms2.SmssyncFconnection}">
			<p class="smssync-url">${"http://&lt;your-ip-address&gt;"+createLink(uri: '/')+"api/1/smssync/"+c.id+"/"}</p>
		</g:if>
		<p class="connection-status"><g:message code="${c.status.i18n}"/></p>
	</div>
</g:link>

