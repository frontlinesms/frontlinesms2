<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<div>
	<div id="tabs">
		<ul>
			<g:each in="['tabs-1' : 'Enter Message', 'tabs-2' : 'Select Recipients', 'tabs-3' : 'Confirm']" var='entry'>
				<g:if test="${configureTabs.contains(entry.key)}">
					<li><a href="#${entry.key}">${entry.value}</a></li>
				</g:if>
			</g:each>
		</ul>

		<g:form action="send" controller="message" method="post">
			<div id="tabs-1" class="${configureTabs.contains('tabs-1') ? '' : 'hide'}">
				<label for="messageText">Enter message</label>
				<g:textArea name="messageText" value="${messageText}" rows="5" cols="40"/>
				<g:link url="#" class="next">Next</g:link>
			</div>
			<div id="tabs-2" class="${configureTabs.contains('tabs-2') ? '' : 'hide'}">
				<label for="address">Add phone number</label>
				<g:textField id="address" name="address"/>
				<g:link url="#" class="add-address">Add</g:link>
				<div id="groups">
					<g:each in="${groupList}" var="entry">
						<div>
										<input type="checkbox"   name="groups" value= "${entry.key}" onclick='selectMembers("${entry.key}", ${entry.value as JSON})'>
						${entry.key}(${entry.value.size()})
				</input>
			</div>
					</g:each>
				</div>

				<div id="contacts">
					<g:each in="${nonExistingRecipients}" var="address">
						<div>
							<input type="checkbox" name="addresses" value="${address}" checked>${address}</input>
						</div>
					</g:each>

					<g:each in="${contactList}" var="contact">
						<div>
											<input contacts="true" class= "" type="checkbox" name="addresses" value="${contact.primaryMobile}" <g:if test="${recipients.contains(contact.primaryMobile)}">checked</g:if>>
						${contact.name ?: contact.primaryMobile} <g:if test="${recipients.contains(contact.secondaryMobile) || recipients.contains(contact.email)}">(Primary)</g:if>
						</input>
				  </div>
				  <div>
						<g:if test="${recipients.contains(contact.secondaryMobile)}">
							<input type="checkbox" name="addresses" value="${contact.secondaryMobile}" checked>
							${contact.name ?: contact.secondaryMobile} (Secondary)
						</input>
						</g:if>
						</div>
				  <div>
						<g:if test="${recipients.contains(contact.email)}">
							<input  type="checkbox" name="addresses" value="${contact.email}" checked>
							${contact.name ?: contact.email} (Email)
						</input>
						</g:if>
						</div>
					</g:each>
				</div>
				<g:link url="#" class="back">Back</g:link>
				<g:link url="#" class="next">Next</g:link>
			</div>
			<div id="tabs-3" class="${configureTabs.contains('tabs-3') ? '' : 'hide'}">
				<label>Do you want to send?</label>
				<g:link url="#" class="back">Back</g:link>
				<g:submitButton name="send-msg" id="sendMsg">Send</g:submitButton>
			</div>
		</g:form>
	</div>
</div>

<script>
	function selectMembers(groupName, allContacts) {
		$.each(allContacts, function(index, value) {
			setValueForCheckBox(groupName, value, isCheckboxSelected(groupName))
		});
	}


	$("input[contacts='true']").bind('click', function() {
		if (!($(this).is(":checked"))) {
			var groups = $(this).attr('class').split(" ")
			$.each(groups, function(index, value) {
				$('input[value=' + "'" + value + "'" + ']').attr('checked', false);
			});
		}
	});

</script>




