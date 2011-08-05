<div id="tabs-2"  class="${configureTabs.contains('tabs-2') ? '' : 'hide'}">
	<label for="address">Add phone number</label>
	<g:textField id="address" name="address"/>
	<g:link url="#" class="add-address">Add</g:link>
	<ol id="groups">
		<g:each in="${groupList}" var="entry">
			<li>
				<input type="checkbox" name="groups" value="${entry.key}">${entry.key}(${entry.value})<br />
			</li>
		</g:each>
	</ol>
	<ol id="contacts">
		<g:each in="${nonExistingRecipients}" var="address">
			<li>
				<input type="checkbox" name="addresses" value="${address}" checked>${address}<br />
			</li>
		</g:each>
		<g:each in="${contactList}" var="contact">
			<li>
				<input type="checkbox" name="addresses" value="${contact.primaryMobile}" <g:if test="${recipients.contains(contact.primaryMobile)}">checked</g:if> >
					${contact.name ?: contact.primaryMobile} <g:if test="${recipients.contains(contact.secondaryMobile) || recipients.contains(contact.email)}">(Primary)</g:if>
				<br />
			</li>
			<g:if test="${recipients.contains(contact.secondaryMobile)}">
				<li>
					<input type="checkbox" name="addresses" value="${contact.secondaryMobile}" checked>
						${contact.name ?: contact.secondaryMobile} (Secondary)
					<br />
				</li>
			</g:if>
			<g:if test="${recipients.contains(contact.email)}">
				<li>
					<input type="checkbox" name="addresses" value="${contact.email}" checked>
						${contact.name ?: contact.email} (Email)
					<br />
				</li>
			</g:if>
		</g:each>
	</ol>	
</div>