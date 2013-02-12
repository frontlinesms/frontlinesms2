<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<div>
	<fsms:recipientSelector/>
	<g:hiddenField name="mobileNumbers" value="${nonContactRecipients?.join(',')}"/>
	<div id="manual-address">
		<label id="manual-label" class="bold" for="address"><g:message code="quickmessage.phonenumber.label" /></label>
		<g:textField id="address" name="address" onkeyup="recipientSelecter.validateAddressEntry();"/>
		<g:link url="#" class="btn add-address" onclick="recipientSelecter.addAddressHandler();" >
			<g:message code="quickmessage.phonenumber.add"/>
		</g:link>
	</div>
	<div id="recipients-list">
		<ul id="groups">
			<g:each in="${groupList}" var="entry" status='i'>
				<li class="group">
					<g:checkBox id="groups-${i}" name="groups" value="${entry.key}" onclick="recipientSelecter.selectMembers(this,'${entry.key}', '${entry.value.name}', ${entry.value.addresses as JSON})" checked="${false}" groupMembers="${entry.value.addresses as JSON}"/>
					<label for="groups-${i}">${entry.value.name} (${entry.value.addresses.size()})</label>
				</li>
			</g:each>
			<g:each in="${nonExistingRecipients}" var="address">
				<li>
					<g:checkBox name="addresses" value="${address}" checked="${true}"/>
					${address}
				</li>
			</g:each>
		</ul>
		<ul id="contacts">
			<g:each in="${contactList}" var="contact" status="i">
				<li class="contact" f-name="${contact.name}" f-number="${contact.mobile}">
					<g:checkBox id="addresses-${i}" name="addresses" value="${contact.mobile}" onclick="recipientSelecter.setContact(this,'${contact.mobile}')" checked="${recipients?.contains(contact.mobile)}"/>
					<label for="addresses-${i}">${contact.name ?: contact.mobile}</label>
					<span class="matched-search-result" id="matched-search-result-${i}">
						<g:message code="contact.mobile.label"/> : ${contact.mobile}
					</span>
				</li>
				<g:if test="${recipients?.contains(contact.email)}">
					<li class="contact">
						<g:checkBox name="addresses" value="${contact.email}" checked="true"/>
						${contact.name ?: contact.email} (<g:message code="contact.email.label"/>)
					</li>
				</g:if>
			</g:each>
		</ul>
	</div>
	<div class="controls">
		<div id="search">
			<g:textField id="searchbox" class='search' name="address" onkeyup="recipientSelecter.searchForContacts();"/>
		</div>
		<div id="recipients-selected"><span id="recipient-count">0</span> <g:message code="quickmessage.selected.recipients"/></div>
	</div>
</div>

